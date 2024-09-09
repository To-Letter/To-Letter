package com.toletter.JWT;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println(path);

        if (path.startsWith("/swagger-ui") || path.contains("/favicon.ico") || path.contains("/swagger-resources") || path.startsWith("/webjars") || path.startsWith("/ws") || path.contains("/v2/api-docs") || path.startsWith("/users/su") || path.startsWith("/users/email") || path.startsWith("/users/kakao")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if(token != null ){ //  accessToken 있으면
            if(jwtTokenProvider.validateToken(token)){ // accessToken 검증
                this.setAuthentication(token);
            } else { // accessToken 검증 실패 시
                if(jwtTokenProvider.validateToken(refreshToken)){ // refreshToken 검증
                    token = jwtTokenProvider.reissueAccessToken(refreshToken);
                    jwtTokenProvider.setHeaderAccessToken(response, token);
                    this.setAuthentication(token);
                    jwtTokenProvider.validateToken(token);
                }
            }
        } else { // accessToken 없으면
            if(jwtTokenProvider.validateToken(refreshToken)){ // refreshToken 검증
                token = jwtTokenProvider.reissueAccessToken(refreshToken);
                jwtTokenProvider.setHeaderAccessToken(response, token);
                this.setAuthentication(token);
                jwtTokenProvider.validateToken(token);
            }
        }
        filterChain.doFilter(request, response);
    }


    // SecurityContext에 Authentication 저장
    private void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
