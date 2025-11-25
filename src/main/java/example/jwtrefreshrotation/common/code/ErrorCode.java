package example.jwtrefreshrotation.common.code;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    String getMessage();
}