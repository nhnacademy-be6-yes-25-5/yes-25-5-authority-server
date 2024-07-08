package com.nhnacademy.yes25.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(FeignException.Forbidden.class)
    public ResponseEntity<Map<String, Object>> handleFeignForbiddenException(FeignException.Forbidden ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        try {
            String jsonMessage = ex.contentUTF8();

            Map<String, Object> parsedMessage = objectMapper.readValue(jsonMessage, HashMap.class);

            errorDetails.put("message", parsedMessage.get("message"));
            errorDetails.put("status", parsedMessage.get("status"));
            errorDetails.put("timestamp", parsedMessage.get("timestamp"));
        } catch (IOException e) {
            errorDetails.put("message", "Error parsing FeignException message");
            errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorDetails.put("timestamp", LocalDateTime.now());
        }

        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }
}
