package com.toletter.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// jwt 토큰 에러 처리를 위해 jwt filter 앞에서 먼저 거쳐감.
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, ex);
        }

    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse res, Throwable ex) throws IOException {
        JwtErrorResponse jwtErrorResponse = new JwtErrorResponse(status, ex);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonErrorResponse = objectMapper.writeValueAsString(jwtErrorResponse);

        res.setStatus(status.value());
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write(jsonErrorResponse);
    }

 }
