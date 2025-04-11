package site.FitUp.main.exception.UserException;


import site.FitUp.main.common.enums.ErrorMessageCode;

public class UserException {

    // 기본 생성자 자동 생성
    public static class LoginFailedException extends RuntimeException {
        public LoginFailedException() {
            super(ErrorMessageCode.LOGIN_FAILED.getMessage());  // 부모 클래스(RuntimeException)의 생성자로 메시지를 전달
        }
    }

    public static class UserIsValidException extends RuntimeException {
        public UserIsValidException() {
            super(ErrorMessageCode.USER_ALREADY_EXISTS.getMessage());
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException() {
            super(ErrorMessageCode.USER_NOT_FOUND.getMessage());
        }
    }
}

