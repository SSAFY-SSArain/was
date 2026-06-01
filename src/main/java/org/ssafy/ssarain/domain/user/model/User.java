package org.ssafy.ssarain.domain.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.ssafy.ssarain.common.model.BaseAuditingEntity;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseAuditingEntity {
    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Size(max = 45)
    @NotNull
    @Column(name = "nickname", nullable = false, unique = true, length = 45)
    private String nickname;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'USER'")
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, String nickname, String password, UserRole role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public static User of(String email, String nickname, String password) {
        return User.builder().email(email).nickname(nickname).password(password).role(UserRole.USER).build();
    }

    public static User of(String email, String nickname, String password, UserRole role) {
        return User.builder().email(email).nickname(nickname).password(password).role(role).build();
    }
}