package example.jwtrefreshrotation.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "invalid_email_or_password"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"사용자 인증이 필요합니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다. 다시 로그인 해주세요."),

    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
