package com.toletter.JWT;

import com.toletter.Entity.User;
import com.toletter.Enums.JwtErrorCode;
import com.toletter.Enums.UserRole;
import com.toletter.Repository.UserRepository;
import com.toletter.Service.Jwt.CustomUserDetailService;
import com.toletter.Service.Jwt.RedisJwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

    @Value("${domain.api}")
    private String domain;

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
        if (!userRepository.existsByEmail(email)) {
            throw new JwtException("유저가 존재하지 않습니다.");
        }

        return createAccessToken(email, userRepository.findByEmail(email).get().getUserRole());
    }

    // refreshToken 재발급
    public String reissueRefreshToken(String newAccessToken, String refreshToken) {
        String email = this.getUserEmail(refreshToken);

        if (!redisJwtService.isValid(email)) {
            throw new JwtException("다시 로그인하세요.");
        }

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new JwtException("유저가 존재하지 않습니다.");
        }

        String newRefreshToken = createRefreshToken(email, user.get().getUserRole());

        redisJwtService.deleteValues(email);
        redisJwtService.setValues(email, newAccessToken, newRefreshToken);

        return newRefreshToken;
    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token"
    public String resolveAccessToken(HttpServletRequest request) {
        if(!request.getHeader("Authorization").isEmpty()){
            return request.getHeader("Authorization").substring(7);
        }
        return null;
    }

    // Request의 Header에서 RefreshToken 값을 가져옵니다. "refreshToken" : "token"
    public String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for(Cookie row : cookies){
            if(row.getName().equals("RefreshToken")){
                return row.getValue();
            }
        }
        return null;
    }

    // Token 만료
    public void expireToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String accessToken = this.resolveAccessToken(httpServletRequest);
        String refreshToken = this.resolveRefreshToken(httpServletRequest);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        Date expiration = claims.getExpiration();
        Date now = new Date();

        // 쿠키 만료
        Cookie cookie = new Cookie("RefreshToken", null);
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
        redisJwtService.setBlackList(accessToken, refreshToken, (expiration.getTime()-now.getTime()));
    }

    // 토큰의 유효성
    public boolean validateToken(HttpServletResponse response, String jwtToken) throws IOException {
        try{
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken);

        } catch (SignatureException | MalformedJwtException e) {
            JwtExceptionFilter.setTokenErrorResponse(response, JwtErrorCode.INVALID_TOKEN,e.getMessage());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            JwtExceptionFilter.setTokenErrorResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN,e.getMessage());
        } catch (IllegalArgumentException e) {
            JwtExceptionFilter.setTokenErrorResponse(response, JwtErrorCode.WRONG_TYPE_TOKEN,e.getMessage());
        } catch (Exception e) {
            JwtExceptionFilter.setTokenErrorResponse(response, JwtErrorCode.WRONG_TOKEN,e.getMessage());
        }
        return true;
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "bearer "+ accessToken); // Bearer(토큰 인증타입) 토큰은 토큰을 소유한 사람에게 액세스 권한을 부여
    }

    // 리프레시 토큰 헤더 설정
    public void setCookieRefreshToken(HttpServletResponse response, String refreshToken) {
        ResponseCookie responseCookie = ResponseCookie.from("RefreshToken", refreshToken)
                .domain(domain)
                .path("/") // 쿠키 경로
                .maxAge(refreshTokenValidTime) // 유효시간
                .secure(true)
                .httpOnly(true) // js를 통해 쿠키에 접근 불가
                .sameSite("None") // 다른 도메인에서의 호출을 막기에 전달이 가능하도록 수정함
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }
}
