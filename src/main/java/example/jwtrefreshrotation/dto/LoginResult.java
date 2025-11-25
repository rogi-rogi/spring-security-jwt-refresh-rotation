package example.jwtrefreshrotation.dto;


import org.springframework.http.ResponseCookie;

public record LoginResult(String accessToken, ResponseCookie refreshCookie) {}
