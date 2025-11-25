package example.jwtrefreshrotation.service;

import example.jwtrefreshrotation.dto.LoginRequest;
import example.jwtrefreshrotation.dto.LoginResult;
import example.jwtrefreshrotation.dto.SignUpRequest;
import example.jwtrefreshrotation.entity.User;
import example.jwtrefreshrotation.repository.UserRepository;
import example.jwtrefreshrotation.security.CustomUserDetails;
import example.jwtrefreshrotation.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Transactional
    public LoginResult login(LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        Long userId = principal.getId();

        String access = jwtProvider.createAccessToken(userId);
        String refresh = jwtProvider.createRefreshToken(userId);

        ResponseCookie refreshCookie = jwtProvider.createRefreshCookie(refresh);
        return new LoginResult(access, refreshCookie);
    }

    @Transactional
    public void signUp(SignUpRequest req) {

        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(req.password());

        // 3) User 저장
        User saved = userRepository.save(new User(req, encodedPassword));
    }

}
