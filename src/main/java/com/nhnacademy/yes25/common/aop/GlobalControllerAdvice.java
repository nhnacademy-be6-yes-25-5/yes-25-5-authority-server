package com.nhnacademy.yes25.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.yes25.common.exception.ApplicationException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import feign.FeignException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorStatus> handleException(ApplicationException e) {
        ErrorStatus errorStatus = e.getErrorStatus();
        return new ResponseEntity<>(errorStatus, HttpStatus.INTERNAL_SERVER_ERROR);
    }

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
