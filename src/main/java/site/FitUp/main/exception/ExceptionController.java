package site.FitUp.main.exception;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import site.FitUp.main.exception.JwtException.JwtException;
import site.FitUp.main.exception.UserException.UserException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionResponse> handleNoSuchElement(NoSuchElementException e) {
        ExceptionResponse response = new ExceptionResponse();
        response.setMessage(e.getMessage());
        response.setCode("404");  // ✅ 404로 변경
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode("400");
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageConversionException(
            HttpMessageConversionException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode("400");
        exceptionResult.setMessage(
                "입력값이 잘못되었습니다. ENUM으로 들어가는 값이나 필수 값을 잘 입력했는지 확인해보세요.: " + ex.getMessage());
        return new ResponseEntity<>(exceptionResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ExceptionResponse> handleHttpServerErrorException(
            HttpServerErrorException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode("500");
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalStateException(IllegalStateException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode("503");
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, HttpStatus.SERVICE_UNAVAILABLE);
    }

    //유저 관련
    @ExceptionHandler(UserException.UserIsValidException.class)
    public ResponseEntity<ExceptionResponse> handleUserIsValidException(
            UserException.UserIsValidException ex) {
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode("409");
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, HttpStatus.CONFLICT);
    }

    //Jwt 토큰 만료 관련
    @ExceptionHandler(JwtException.TokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleJwtTokenExpiredException(
            JwtException.TokenExpiredException ex){
        ExceptionResponse exceptionResult = new ExceptionResponse();
        exceptionResult.setCode("403");
        exceptionResult.setMessage(ex.getMessage());
        return new ResponseEntity<>(exceptionResult, HttpStatus.UNAUTHORIZED);
    }

}
