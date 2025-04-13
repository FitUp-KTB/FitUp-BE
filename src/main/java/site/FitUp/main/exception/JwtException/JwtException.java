package site.FitUp.main.exception.JwtException;

import lombok.NoArgsConstructor;
import site.FitUp.main.common.enums.ErrorMessageCode;

public class JwtException {


    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException() {
            super(ErrorMessageCode.JWT_TOKEN_EXPIRED.getMessage());
        }
    }

    @NoArgsConstructor
    public static class TokenNotFoundException extends RuntimeException {
        public TokenNotFoundException(String message) {
            super(message);
        }
    }

    @NoArgsConstructor
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }
    @NoArgsConstructor
    public static class DuplicateRequestException extends RuntimeException {
        public DuplicateRequestException(String message) {
            super(message);
        }
    }
}
