package com.toletter.Config;

import com.toletter.JWT.JwtAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // HTTP Basic 인증을 비활성화
                .csrf().disable(); // 세션을 사용하지 않고 JWT 토큰을 활용하여 진행, csrf 토큰검사를 비활성화
        http.cors();

        http.authorizeRequests()
                .antMatchers("/users/su/**", "/users/kakao/", "/users/email/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout() // 로그아웃 설정
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    @Override
    public void configure(WebSecurity webSecurity){
        webSecurity.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/ws/**", "/webjars/**", "/swagger-ui/**", "/webjars", "/favicon.ico");
    }
}
