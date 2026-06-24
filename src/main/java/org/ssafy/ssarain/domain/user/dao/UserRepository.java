package org.ssafy.ssarain.domain.user.dao;

import org.ssafy.ssarain.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Optional<User> findByUidAndDeletedAtIsNull(UUID uid);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByNameAndDeletedAtIsNull(String name);
    
    long countAllByUidInAndDeletedAtIsNull(List<UUID> uids);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.deletedAt IS NULL
              AND (:search = ''
                    OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
              AND NOT EXISTS (
                    SELECT 1
                    FROM BrainMember bm
                    WHERE bm.bmid.bid = :bid
                      AND bm.bmid.uid = u.uid
              )
            """)
    Page<User> searchUsersAvailableForBrain(int bid, String search, Pageable pageable);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.deletedAt IS NULL
              AND u.uid <> :requesterUid
              AND (:search = ''
                    OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY u.name ASC
            """)
    Page<User> searchUsers(
            @Param("search") String search,
            @Param("requesterUid") UUID requesterUid,
            Pageable pageable
    );
}
