package com.toletter.JWT;

import com.toletter.Enums.JwtErrorCode;
import com.toletter.Service.Jwt.RedisJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisJwtService redisJwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println(path);

        if (path.startsWith("/swagger-ui") || path.contains("/favicon.ico") || path.contains("/swagger-resources") || path.startsWith("/webjars") || path.startsWith("/ws") || path.contains("/v2/api-docs") || path.startsWith("/users/su") || path.startsWith("/users/email") || path.startsWith("/users/find") ||path.startsWith("/kakao/su") || path.contains("/users/kakao")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveAccessToken(request);

        if(token != null ){ //  accessToken 있으면
            if(jwtTokenProvider.validateToken(response, token) && !redisJwtService.isValidBlackList(token)){ // accessToken 검증
                this.setAuthentication(token);
            } else { // accessToken 검증 실패 시
                String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
                log.info("accessToken 만료");
                if(jwtTokenProvider.validateToken(response, refreshToken) && !redisJwtService.isValidBlackList(token)){ // refreshToken 검증
                    String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);
                    String newRefreshToken = jwtTokenProvider.reissueRefreshToken(newAccessToken, refreshToken);

                    jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
                    jwtTokenProvider.setCookieRefreshToken(response, newRefreshToken);
                    this.setAuthentication(newAccessToken);
                } else {
                    JwtExceptionFilter.setTokenErrorResponse(response, JwtErrorCode.EXPIRED_TOKEN,"refreshToken 만료되었습니다. 다시 로그인하세요");
                    return;
                }
            }
        } else {
            JwtExceptionFilter.setTokenErrorResponse(response, JwtErrorCode.WRONG_TYPE_TOKEN,"빈 문자열입니다. 다시 로그인해주세요.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    // SecurityContext에 Authentication 저장
    private void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
