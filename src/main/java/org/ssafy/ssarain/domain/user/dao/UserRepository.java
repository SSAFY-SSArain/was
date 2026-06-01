package org.ssafy.ssarain.domain.user.dao;

import org.ssafy.ssarain.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID userId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
