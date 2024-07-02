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

        if (path.startsWith("/swagger-ui") || path.contains("/favicon.ico") || path.contains("/swagger-resources") || path.startsWith("/webjars") || path.startsWith("/ws") || path.contains("/v2/api-docs") || path.contains("/users/signup") || path.contains("/users/login") || path.contains("/users/confirmID") || path.contains("/users/confirmNickname") || path.contains("/users/email-auth") || path.contains("/users/email-verify") || path.startsWith("/users/kakao")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveAccessToken(request);

        if(token == null){
            String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
            if (jwtTokenProvider.validateToken(refreshToken)) {
                try {
                    token = jwtTokenProvider.reissueAccessToken(refreshToken);
                } catch (org.json.simple.parser.ParseException e) {
                    throw new RuntimeException(e);
                }
                jwtTokenProvider.setHeaderAccessToken(response, token);
                this.setAuthentication(token);
            }
        } else if(token != null){
            if(jwtTokenProvider.validateToken(token)){
                this.setAuthentication(token);
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
