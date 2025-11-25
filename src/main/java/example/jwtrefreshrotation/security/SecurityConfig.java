package example.jwtrefreshrotation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtFilter jwtFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(CsrfConfigurer::spa)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/check/**").permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(eh -> eh
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint(objectMapper))
                    .accessDeniedHandler(new RestAccessDeniedHandler(objectMapper))
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }
}
