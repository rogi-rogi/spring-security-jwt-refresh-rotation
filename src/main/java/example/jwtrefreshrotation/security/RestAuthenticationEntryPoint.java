package example.jwtrefreshrotation.security;

import example.jwtrefreshrotation.common.APIResponse;
import example.jwtrefreshrotation.common.code.AuthErrorCode;
import example.jwtrefreshrotation.common.code.ErrorCode;
import example.jwtrefreshrotation.common.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode code = AuthErrorCode.UNAUTHORIZED;

        if (authException instanceof AuthException ae) {
            code = ae.getErrorCode();
        }

        log.warn("[Filter 401] {} {} - {}", request.getMethod(), request.getRequestURI(), code.getMessage());

        APIResponse<?> body = APIResponse.error(code);

        response.setStatus(code.getStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}