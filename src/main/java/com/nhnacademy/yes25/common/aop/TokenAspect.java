package com.nhnacademy.yes25.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 인증 토큰 관리를 위한 AOP(Aspect-Oriented Programming) 구성 요소입니다.
 * Controller의 메서드 실행 전후에 토큰 처리 로직을 수행합니다.
 */
@Aspect
@Component
public class TokenAspect {

    /**
     * Controller의 특정 메서드들을 대상으로 하는 Around 어드바이스입니다.
     * 요청에서 토큰을 추출하고, 응답에 새로운 토큰을 추가하는 역할을 합니다.
     *
     * 로그인 메서드, accessJwt 재발급 메서드는 제외합니다.
     *
     * @param joinPoint 메서드 실행 지점을 나타내는 ProceedingJoinPoint 객체
     * @return 원본 메서드의 실행 결과 또는 수정된 ResponseEntity
     * @throws Throwable 메서드 실행 중 발생할 수 있는 모든 예외
     */
    @Around("execution(* com.nhnacademy.yes25.presentation.controller.*.*(..))" +
            "&& !execution(* com.nhnacademy.yes25.presentation.controller.AuthController.tokenRefresh(..))" +
            "&& !execution(* com.nhnacademy.yes25.presentation.controller.AuthController.findLoginUserByEmail(..))")
    public Object aroundControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String accessJwt = null;
        String refreshJwt = null;

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        String accessJwtHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshJwtHeader = request.getHeader("Refresh-Token");

        if (checkJwtHeaderValidity(accessJwtHeader)) {
            accessJwt = accessJwtHeader.substring("Bearer ".length());
        }

        if (refreshJwtHeader != null) {
            refreshJwt = refreshJwtHeader;
        }

        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity<?> responseEntity) {

            if (isNotEmptyTokens(accessJwt, refreshJwt)) {
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " +accessJwt);
                response.setHeader("Refresh-Token", refreshJwt);
            }

            return responseEntity;
        }

        return result;
    }

    public boolean checkJwtHeaderValidity (String accessJwtHeader) {
        return accessJwtHeader != null && accessJwtHeader.startsWith("Bearer ");
    }

    public boolean isNotEmptyTokens(String accessJwt, String refreshJwt) {
        return accessJwt != null && refreshJwt != null;
    }

}