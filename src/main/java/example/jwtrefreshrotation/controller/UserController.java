package example.jwtrefreshrotation.controller;


import example.jwtrefreshrotation.dto.UserProfileResult;
import example.jwtrefreshrotation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResult> me() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalArgumentException("Unauthenticated");
        }

        Long userId = (Long) auth.getPrincipal();

        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
}
