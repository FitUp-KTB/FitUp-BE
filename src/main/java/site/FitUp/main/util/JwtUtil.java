package site.FitUp.main.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    private static String SECRET_KEY;
    private static Long ACCESS_TOKEN_EXPIRATION;
    private static Long REFRESH_TOKEN_EXPIRATION;

    // 생성자에서 주입받도록 수정
    public JwtUtil(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.ACCESS_TOKEN_EXPIRATION}") Long accessTokenExpiration,
            @Value("${jwt.REFRESH_TOKEN_EXPIRATION}") Long refreshTokenExpiration) {
        this.SECRET_KEY = secretKey;
        this.ACCESS_TOKEN_EXPIRATION = accessTokenExpiration;
        this.REFRESH_TOKEN_EXPIRATION = refreshTokenExpiration;
    }

    public static String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .compact();
    }

    public static String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .compact();
    }

    public static String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("Authentication is NULL in DELETE request");
        }

        log.info("Authenticated userId: {}", authentication.getName());
        return authentication.getName();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUserId(String bearerToken) {
        String token = bearerToken.substring(7);
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build();
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // 토큰 검증
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰이 만료되었는지 확인
    public boolean isTokenExpired(String token) {
        try {
            return validateToken(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }


}
