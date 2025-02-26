package site.FitUp.main.util;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "secretsecretsecretsecretsecretsecretsecretsecretsecret";  // 비밀 키 (실제 환경에서는 안전하게 관리해야 함)

    // JWT 토큰 생성 메서드
    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // JWT 토큰에서 userId를 추출하는 메서드 (Bearer 토큰 처리)
    public static String extractUserId(String bearerToken) {
        // Bearer 문자열을 제거하여 토큰 부분만 추출
        String token = bearerToken.substring(7);  // "Bearer " 길이는 7
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)  // 서명 검증을 위한 비밀 키 설정
                .build();
        Claims claims = parser.parseClaimsJws(token).getBody();  // 서명된 JWT 파싱
        return claims.getSubject();  // JWT의 subject는 userId로 저장됩니다.
    }
}
