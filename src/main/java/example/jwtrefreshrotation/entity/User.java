package example.jwtrefreshrotation.entity;


import example.jwtrefreshrotation.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String password;

    public User(SignUpRequest req, String encodedPassword) {
        this.email = req.email();
        this.password = encodedPassword;
    }
}
