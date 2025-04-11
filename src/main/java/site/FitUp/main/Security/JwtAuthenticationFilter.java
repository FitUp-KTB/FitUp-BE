package site.FitUp.main.Security;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.FitUp.main.util.JwtUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기
    private final List<String> excludedPaths; // ✅ 필터 제외할 경로 리스트

    public JwtAuthenticationFilter(JwtUtil jwtUtil, List<String> excludedPaths) {
        this.jwtUtil = jwtUtil;
        this.excludedPaths = excludedPaths;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // DELETE 요청이 아니라면, 인증 제외할 경로 확인
        if (!method.equals("DELETE") && excludedPaths.contains(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "토큰이 존재하지 않습니다.");
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);
            String userId = claims.getSubject();

            if (userId != null) {
                UserDetails userDetails = new User(userId, "", Collections.emptyList());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // 🔹 액세스 토큰 만료 시 리프레시 토큰을 사용하여 재발급 시도
            String refreshToken = httpRequest.getHeader("refreshToken");
            log.info("Refresh token: {}", refreshToken);
            if (refreshToken != null && !refreshToken.isEmpty()) {
                try {
                    Claims refreshClaims = jwtUtil.validateToken(refreshToken);
                    String userId = refreshClaims.getSubject();

                    // 새 accessToken 생성
                    String newAccessToken = jwtUtil.generateAccessToken(userId);
                    log.info("New access token: {}", newAccessToken);
                    // 🔹 새 accessToken을 응답 헤더에 추가
                    httpResponse.setHeader("Authorization", "Bearer " + newAccessToken);
                    httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                    // 🔹 응답을 계속 진행 (클라이언트가 새 accessToken을 사용할 수 있도록)
                    chain.doFilter(request, response);
                    return;
                } catch (Exception ex) {
                    sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token이 유효하지 않습니다.");
                    return;
                }
            }

            sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Access Token이 만료되었습니다. Refresh Token을 사용해 주세요.");
            return;
        } catch (Exception e) {
            log.error(e.getMessage());
            sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return;
        }
    }


    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        response.reset();
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status);
        errorDetails.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        response.getWriter().flush();
    }

}
