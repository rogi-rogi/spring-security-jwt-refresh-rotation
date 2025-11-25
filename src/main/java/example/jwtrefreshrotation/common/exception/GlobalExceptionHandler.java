package example.jwtrefreshrotation.common.exception;


import example.jwtrefreshrotation.common.APIResponse;
import example.jwtrefreshrotation.common.code.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<APIResponse<?>> handleAppException(
            AppException e,
            HttpServletRequest request
    ) {
        ErrorCode code = e.getErrorCode();

        log.warn("[AppException {}] {} {} - {}", code.getStatus(), request.getMethod(), request.getRequestURI(), e.getMessage());

        return ResponseEntity.status(code.getStatus())
                .body(APIResponse.error(code));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<?>> handleAnyException(
            Exception e,
            HttpServletRequest request
    ) {
        log.warn("[Exception] {} {} - {}", request.getMethod(), request.getRequestURI(), e.getMessage());

        HttpStatus status = resolveStatus(e);
        return  ResponseEntity.status(status.value())
                .body(APIResponse.error(status));
    }
    public HttpStatus resolveStatus(Exception e) {
        if (e instanceof HttpRequestMethodNotSupportedException) {
            return HttpStatus.METHOD_NOT_ALLOWED; // 405
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
