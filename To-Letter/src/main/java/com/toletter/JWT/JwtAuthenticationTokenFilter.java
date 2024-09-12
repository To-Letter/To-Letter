package com.toletter.JWT;

import com.toletter.Enums.JwtErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import org.json.simple.JSONObject;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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

        try {
            if(jwtTokenProvider.validateToken(refreshToken) && path.contains("/users/reissue")){
                token = jwtTokenProvider.reissueAccessToken(refreshToken);
                jwtTokenProvider.setHeaderAccessToken(response, token);
                this.setAuthentication(token);
                jwtTokenProvider.validateToken(token);
            } else {
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
                }
            }
        } catch (SecurityException | MalformedJwtException e) {
            setErrorResponse(response, JwtErrorCode.INVALID_TOKEN);
            return;
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, JwtErrorCode.EXPIRED_TOKEN);
            return;
        } catch (UnsupportedJwtException e) {
            setErrorResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
            return;
        } catch (IllegalArgumentException e) {
            setErrorResponse(response, JwtErrorCode.WRONG_TYPE_TOKEN);
            return;
        } catch (Exception e) {
            setErrorResponse(response, JwtErrorCode.WRONG_TOKEN);
            return;
        }
        filterChain.doFilter(request, response);
    }

    // 에러처리
    public void setErrorResponse(HttpServletResponse response, JwtErrorCode code) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("code", code.getCode());
        json.put("message", code.getMessage());

        response.getWriter().print(json);
        response.getWriter().flush();
    }


    // SecurityContext에 Authentication 저장
    private void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
