package org.ssafy.ssarain.domain.user.dao;

import org.ssafy.ssarain.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUid(UUID uid);

    boolean existsByEmail(String email);

    boolean existsByName(String name);
    
    long countAllByUidIn(List<UUID> uids);

    @Query("""
            SELECT u
            FROM User u
            WHERE (:search = ''
                    OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
              AND NOT EXISTS (
                    SELECT 1
                    FROM BrainMember bm
                    WHERE bm.bmid.bid = :bid
                      AND bm.bmid.uid = u.uid
              )
            """)
    List<User> searchUsersAvailableForBrain(int bid, String search);
}
