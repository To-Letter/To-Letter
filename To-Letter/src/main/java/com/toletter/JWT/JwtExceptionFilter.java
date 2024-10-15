package com.toletter.JWT;

import com.toletter.Enums.JwtErrorCode;
import com.toletter.Error.ErrorCode;
import groovy.util.logging.Slf4j;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// jwt 토큰 에러 처리를 위해 jwt filter 앞에서 먼저 거쳐감.
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e){
            setErrorResponse(response, ErrorCode.UNAUTHORIZED_EXCEPTION, e.getMessage());
        }
    }

    public static void setTokenErrorResponse(HttpServletResponse response, JwtErrorCode code, String errorMessage) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("code", code.getCode());
        json.put("message", code.getMessage());
        json.put("error", errorMessage);

        response.getWriter().print(json);
    }

    public static void setErrorResponse(HttpServletResponse response, ErrorCode code, String errorMessage) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code.getCode());

        json.put("responseCode", code.getCode());
        json.put("responseStatus", code.getMessage());
        json.put("responseMessage", errorMessage);

        response.getWriter().print(json);
    }

}