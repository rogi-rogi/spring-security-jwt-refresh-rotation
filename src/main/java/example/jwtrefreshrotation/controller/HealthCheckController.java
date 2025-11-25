package example.jwtrefreshrotation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/check")
public class HealthCheckController {

    @GetMapping("/cors")
    public ResponseEntity<?> checkCors() {
        return ResponseEntity.ok(Map.of("msg", "get-req: cors"));
    }

    @GetMapping("/csrf")
    public ResponseEntity<?> checkGetCsrf() {
        return ResponseEntity.ok(Map.of("msg", "get-req: csrf"));
    }

    @PostMapping("/csrf")
    public ResponseEntity<?> checkPostCsrf() {
        return ResponseEntity.ok(Map.of("msg", "post-req: csrf"));
    }
}
