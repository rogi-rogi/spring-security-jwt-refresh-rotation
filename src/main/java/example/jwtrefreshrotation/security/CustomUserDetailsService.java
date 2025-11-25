package example.jwtrefreshrotation.security;

import example.jwtrefreshrotation.entity.User;
import example.jwtrefreshrotation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. 로그인을 시도한 사용자의 이메일로 DB에서 조회
        User user = userRepository.findByEmail(email).orElseThrow();


        // 2. CustomUserDetails로 래핑해서 반환
        return new CustomUserDetails(user);
    }
}
