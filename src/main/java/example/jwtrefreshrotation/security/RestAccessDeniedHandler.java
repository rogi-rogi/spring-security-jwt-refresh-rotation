package example.jwtrefreshrotation.security;


import example.jwtrefreshrotation.common.APIResponse;
import example.jwtrefreshrotation.common.code.AuthErrorCode;
import example.jwtrefreshrotation.common.code.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ErrorCode code = AuthErrorCode.FORBIDDEN;

        log.warn("[Filter 403] {} {} - {}", request.getMethod(), request.getRequestURI(), code.getMessage());

        APIResponse<?> body = APIResponse.error(code);

        response.setStatus(code.getStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}