package com.toletter.JWT;

import com.toletter.Enums.UserRole;
import com.toletter.Repository.UserRepository;
import com.toletter.Error.*;
import com.toletter.Service.Jwt.CustomUserDetailService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.*;
import java.security.Key;
import java.util.*;

@Component
@RequiredArgsConstructor
@Transactional
public class JwtTokenProvider {
    private final UserRepository userRepository;
    private final CustomUserDetailService customUserDetailService;

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
    public String createAccessToken(String id,  UserRole userRole) {
        return this.createToken(id, userRole, accessTokenValidTime);
    }
    // Refresh Token 생성.
    public String createRefreshToken(String id, UserRole userRole) {
        return this.createToken(id, userRole, refreshTokenValidTime);
    }

    // Create token
    public String createToken(String id, UserRole userRole, long tokenValid) {
        Claims claims = Jwts.claims().setSubject(id); // claims 생성 및 payload 설정
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
        UserDetails userDetails = customUserDetailService.loadUserByUsername(this.getUserId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserId(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();

        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰에서 만료 시간 추출
    public Date getExpireDate(String refreshToken){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        return claims.getExpiration();
    }

    // accessToken 재발행
    public String reissueAccessToken(String refreshToken) throws ParseException {
        String id = this.getUserId(refreshToken);
        if (id == null) {
            throw new ErrorException("401", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        String token = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken)
                .getBody().get("token", String.class);

        return createAccessToken(id, userRepository.findById(id).get().getUserRole());
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

    // Token 만료
    public void expireToken(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Date expiration = claims.getExpiration();
        Date now = new Date();
        if (now.after(expiration)) {
            // 토큰 만료 시 해당 데이터 삭제 -> 아직 h2 데이터베이스 연결안함.
        }
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            throw new ErrorException("토큰 만료", ErrorCode.UNAUTHORIZED_EXCEPTION);
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("JWT token is unsupported", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty", e);
        } catch (SignatureException e) {
            throw new SignatureException("JWT signature verification failed", e);
        }
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
