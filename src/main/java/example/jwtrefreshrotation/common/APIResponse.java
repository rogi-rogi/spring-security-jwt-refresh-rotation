package example.jwtrefreshrotation.common;

import example.jwtrefreshrotation.common.code.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Map;

public record APIResponse<T>(
        String message,
        T data

) {
    public static APIResponse<?> error(ErrorCode code) {
        return new APIResponse<>(code.getMessage(), Map.of());
    }
    public static APIResponse<?> error(HttpStatus code) {
        return new APIResponse<>(code.getReasonPhrase(), Map.of());
    }
}
