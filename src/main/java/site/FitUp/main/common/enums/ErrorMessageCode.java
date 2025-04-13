package site.FitUp.main.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessageCode {

    USER_ALREADY_EXISTS("이미 회원정보가 존재합니다."),
    USER_NOT_FOUND("유저를 찾을 수 없거나 탈퇴한 계정입니다."),
    USER_ALREADY_DELETED("유저를 찾을 수 없거나 이미 탈퇴한 계정입니다."),
    LOGIN_FAILED("이메일 또는 비밀번호가 잘못되었거나, 탈퇴한 계정입니다."),
    INVALID_PASSWORD("이메일 또는 비밀번호가 잘못되었습니다."),

    // 게시물 관련
    POST_NOT_FOUND("게시물을 찾을 수 없거나 삭제된 게시물입니다."),
    POST_NOT_OWNER("해당 게시물의 작성자가 아닙니다."),

    // 댓글 관련
    COMMENT_NOT_FOUND("해당 댓글을 찾을 수 없습니다."),
    COMMENT_NOT_OWNER("해당 댓글의 작성자가 아닙니다."),



    // JWT 만료 관련
    JWT_TOKEN_EXPIRED("엑세스토큰이 만료되었습니다. 새 엑세스토큰을 받아주세요.");
    private final String message;
}
