package com.nhnacademy.yes25.common.jwt;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.common.exception.JwtException;
import com.nhnacademy.yes25.common.exception.MissingJwtHeaderException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 각 API 적용을 테스트 하기 위해 구현한 JWT 인증 처리 필터 클래스입니다.
 * 요청의 Authorization 헤더에서 JWT를 추출하고 검증하여 인증을 수행합니다.
 */
@RequiredArgsConstructor
@Component
public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final TokenInfoService tokenInfoService;

    /**
     * JWT 인증 필터를 수행합니다.
     * 특정 경로(/auth/login, /users, /auth/refresh)에 대해서는 필터를 적용하지 않습니다.
     * 유효한 토큰이 있는 경우, 해당 사용자의 인증 정보를 SecurityContext에 설정합니다.
     *
     * @param servletRequest 서블릿 요청
     * @param servletResponse 서블릿 응답
     * @param filterChain 필터 체인
     * @throws IOException IO 예외 발생 시
     * @throws ServletException 서블릿 예외 발생 시
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getServletPath();

        if (isExcludedPath(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String token = getToken(request);

        String uuid = jwtProvider.getUuidFromToken(token);

        //다른 api의 경우 아래 단계에서 인증 서버로 feign 요청을 보내서 tokenInfo를 가져와야 함
        ReadTokenInfoResponse tokenInfo = tokenInfoService.getByUuid(uuid);

        JwtUserDetails jwtUserDetails = JwtUserDetails.of(tokenInfo.customerId(), tokenInfo.role());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                jwtUserDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + tokenInfo.role()))
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * HTTP 요청의 Authorization 헤더에서 JWT를 추출합니다.
     *
     * @param request HTTP 요청
     * @return 추출된 JWT 문자열
     * @throws JwtException JWT를 찾을 수 없는 경우 발생
     */
    private String getToken(HttpServletRequest request) {

        String accessJwtHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (checkJwtHeaderValidity(accessJwtHeader)) {
            return accessJwtHeader.substring(7);
        }

        throw new MissingJwtHeaderException(
                ErrorStatus.toErrorStatus("헤더에서 토큰을 찾을 수 없습니다.", 401, LocalDateTime.now())
        );
    }

    /**
     * JWT 헤더의 유효성을 검사합니다.
     *
     * @param accessJwtHeader Authorization 헤더 값
     * @return 헤더가 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean checkJwtHeaderValidity (String accessJwtHeader) {
        return accessJwtHeader != null && accessJwtHeader.startsWith("Bearer ");
    }

    /**
     * 주어진 경로가 JWT 인증에서 제외되는 경로인지 확인합니다.
     *
     * @param path 확인할 요청 경로
     * @return 제외 경로인 경우 true, 그렇지 않으면 false
     */
    private boolean isExcludedPath(String path) {
        return path.startsWith("/auth/login") || path.equals("/users") || path.equals("/auth/refresh") || path.startsWith("/auth/info")
            || path.startsWith("/auth/dormant") || path.startsWith("/authority/v3/api-docs") || path.matches("/authority/swagger-ui/index.html")
                || path.matches("/authority/swagger-ui.html") || path.startsWith("/authority/swagger-ui");
    }

}