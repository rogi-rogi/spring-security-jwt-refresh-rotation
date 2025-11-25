package example.jwtrefreshrotation.common.exception;

import example.jwtrefreshrotation.common.code.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthException extends AuthenticationException {
    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}