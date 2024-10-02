package com.toletter.JWT;

import com.toletter.Entity.User;
import com.toletter.Enums.JwtErrorCode;
import com.toletter.Enums.UserRole;
import com.toletter.Repository.UserRepository;
import com.toletter.Error.*;
import com.toletter.Service.Jwt.CustomUserDetailService;
import com.toletter.Service.Jwt.RedisJwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.Key;
import java.util.*;

@Component
@RequiredArgsConstructor
@Transactional
public class JwtTokenProvider {
    private final UserRepository userRepository;
    private final CustomUserDetailService customUserDetailService;
    private final RedisJwtService redisJwtService;
    // 키
    @Value("${jwt.secret}")
    private String secretKey;
    // 액세스 토큰 유효시간 | 1h
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenValidTime;
    // 리프레시 토큰 유효시간 | 7d
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenValidTime;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct // 의존성 주입 후, 초기화를 수행
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Access Token 생성.
    public String createAccessToken(String email,  UserRole userRole) {
        return this.createToken(email, userRole, accessTokenValidTime);
    }
    // Refresh Token 생성.
    public String createRefreshToken(String email, UserRole userRole) {
        return this.createToken(email, userRole, refreshTokenValidTime);
    }

    // Create token
    public String createToken(String email, UserRole userRole, long tokenValid) {
        Claims claims = Jwts.claims().setSubject(email); // claims 생성 및 payload 설정
        claims.put("roles", userRole.toString()); // 권한 설정, key/ value 쌍으로 저장

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Date date = new Date();

        return Jwts.builder()
                .setClaims(claims) // 발행 유저 정보 저장
                .setIssuedAt(date) // 발행 시간 저장
                .setExpiration(new Date(date.getTime() + tokenValid)) // 토큰 유효 시간 저장
                .signWith(key, SignatureAlgorithm.HS256) // 해싱 알고리즘 및 키 설정
                .compact(); // 생성
    }

    // JWT 토큰에서 인증 정보 조회
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(this.getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserEmail(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();

        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    // accessToken 재발행
    public String reissueAccessToken(String refreshToken) {
        String email = this.getUserEmail(refreshToken);
        if (email == null) {
            throw new ErrorException("유저가 존재하지 않습니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        return createAccessToken(email, userRepository.findByEmail(email).get().getUserRole());
    }

    // refreshToken 재발급
    public String reissueRefreshToken(String refreshToken) {
        String email = this.getUserEmail(refreshToken);

        if (!redisJwtService.isValid(email)) {
            throw new ErrorException("다시 로그인하세요.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new ErrorException("유저가 존재하지 않습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        String newRefreshToken = createRefreshToken(email, user.get().getUserRole());

        redisJwtService.deleteValues(email);
        redisJwtService.setValues(email, newRefreshToken);

        return newRefreshToken;
    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token"
    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("Authorization") != null )
            return request.getHeader("Authorization").substring(7);

        return null;
    }

    // Request의 Header에서 RefreshToken 값을 가져옵니다. "refreshToken" : "token"
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getHeader("refreshToken") != null )
            return request.getHeader("refreshToken").substring(7);
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(HttpServletResponse response, String jwtToken) throws IOException {
        try{
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken);

        } catch (SecurityException | MalformedJwtException e) {
            setErrorResponse(response, JwtErrorCode.INVALID_TOKEN, e.getMessage());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            setErrorResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            setErrorResponse(response, JwtErrorCode.WRONG_TYPE_TOKEN, e.getMessage());
        } catch (Exception e) {
            setErrorResponse(response, JwtErrorCode.WRONG_TOKEN, e.getMessage());
        }
        return true;
    }

    // 에러처리
    public void setErrorResponse(HttpServletResponse response, JwtErrorCode code, String errorMessage) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("code", code.getCode());
        json.put("message", code.getMessage());
        json.put("error", errorMessage);

        response.getWriter().print(json);
        response.getWriter().flush();
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "bearer "+ accessToken); // Bearer(토큰 인증타입) 토큰은 토큰을 소유한 사람에게 액세스 권한을 부여
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("refreshToken", "bearer "+ refreshToken);
    }
}
