package example.jwtrefreshrotation.controller;


import example.jwtrefreshrotation.dto.TokenResponse;
import example.jwtrefreshrotation.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRefreshController {
    private final JwtProvider jwtProvider;

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request) {

        String refreshToken = jwtProvider.resolveRefreshToken(request)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token is missing"));

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Long userId = jwtProvider.getUserIdFromRefresh(refreshToken);

        // db로부터 실제 존재하는 사용자인지 추가 검사 필요

        String newAccess = jwtProvider.createAccessToken(userId);

        String newRefresh = jwtProvider.createRefreshToken(userId);
        ResponseCookie refreshCookie = jwtProvider.createRefreshCookie(newRefresh);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new TokenResponse(newAccess));
    }
}
