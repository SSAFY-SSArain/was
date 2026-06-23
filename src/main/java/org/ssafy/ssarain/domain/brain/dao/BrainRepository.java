package org.ssafy.ssarain.domain.brain.dao;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.brain.model.Brain;

public interface BrainRepository extends JpaRepository<Brain, Integer> {
    
    boolean existsByName(String name);
    
    Page<Brain> findByNameContaining(String name, Pageable pageable);

    @Query("""
            SELECT b
            FROM Brain b
            WHERE (:name IS NULL OR :name = '' OR b.name LIKE CONCAT('%', :name, '%'))
                AND (
                    :includeJoined = true
                    OR :uid IS NULL
                    OR NOT EXISTS (
                        SELECT 1
                        FROM BrainMember bm
                        WHERE bm.bmid.bid = b.bid
                            AND bm.bmid.uid = :uid
                    )
                )
            """)
    Page<Brain> searchBrains(String name, UUID uid, boolean includeJoined, Pageable pageable);
    
}
