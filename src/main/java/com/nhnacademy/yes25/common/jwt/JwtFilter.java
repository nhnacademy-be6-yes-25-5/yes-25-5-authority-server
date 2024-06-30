package com.nhnacademy.yes25.common.jwt;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.common.exception.JwtException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final TokenInfoService tokenInfoService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getServletPath();

        // "/auth/login", login에 필요한 정보 요청 경로는 제외
        if (path.startsWith("/auth/login") || path.startsWith("/users")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = getToken(request);

        if (jwtProvider.isValidToken(token)) {
            String uuid = jwtProvider.getUuidFromToken(token);
            ReadTokenInfoResponse tokenInfo = tokenInfoService.getByUuid(uuid);
            JwtUserDetails jwtUserDetails = JwtUserDetails.of(tokenInfo.customerId(), tokenInfo.role());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    jwtUserDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + tokenInfo.role()))
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getToken(HttpServletRequest request) {

        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        throw new JwtException(
                ErrorStatus.toErrorStatus("헤더에서 토큰을 찾을 수 없습니다.", 401, LocalDateTime.now())
        );
    }

}