package example.jwtrefreshrotation.dto;

public record LoginRequest(
    String email,
    String password
) {}
