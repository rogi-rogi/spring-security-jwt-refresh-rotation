package example.jwtrefreshrotation.common.exception;

import example.jwtrefreshrotation.common.code.ErrorCode;
import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
