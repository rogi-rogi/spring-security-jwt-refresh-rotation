package example.jwtrefreshrotation.service;


import example.jwtrefreshrotation.dto.UserProfileResult;
import example.jwtrefreshrotation.entity.User;
import example.jwtrefreshrotation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResult getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserProfileResult(user.getEmail());
    }
}