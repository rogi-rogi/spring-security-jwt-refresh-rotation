package example.jwtrefreshrotation.controller;


import example.jwtrefreshrotation.dto.LoginRequest;
import example.jwtrefreshrotation.dto.LoginResponse;
import example.jwtrefreshrotation.dto.LoginResult;
import example.jwtrefreshrotation.dto.SignUpRequest;
import example.jwtrefreshrotation.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest req
    ) {

        LoginResult loginResult = authService.login(req);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, loginResult.refreshCookie().toString())
                .body(new LoginResponse(loginResult.accessToken()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest req) {
        authService.signUp(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
