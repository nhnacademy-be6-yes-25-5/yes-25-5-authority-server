//package com.nhnacademy.yes25.common.filter;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.crypto.SecretKey;
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class JWTFilter extends OncePerRequestFilter {
//
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String BEARER_PREFIX = "Bearer ";
//    private static final String SECRET_KEY = "your_secret_key_here"; // 실제로는 환경 변수에서 가져와야 함
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
//
//        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
//            String jwt = authorizationHeader.substring(BEARER_PREFIX.length());
//
//            try {
//                SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//                Claims claims = Jwts.parser()
//                        .setSigningKey(key)
//                        .build()
//                        .parseClaimsJws(jwt)
//                        .getBody();
//
//                String userId = claims.getSubject();
//                List<String> roles = (List<String>) claims.get("roles");
//
//                // 사용자 정보와 권한 검사 로직 추가
//                // 예: 권한 기반 접근 제어 구현
//
//                filterChain.doFilter(request, response);
//            } catch (Exception e) {
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                response.getWriter().write("Invalid JWT token");
//                return;
//            }
//        } else {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.getWriter().write("Missing Authorization header");
//            return;
//        }
//    }
//}