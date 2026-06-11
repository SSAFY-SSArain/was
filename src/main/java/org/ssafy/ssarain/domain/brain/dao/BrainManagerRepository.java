package org.ssafy.ssarain.domain.brain.dao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.brain.model.BrainManager;

public interface BrainManagerRepository extends JpaRepository<BrainManager, Integer> {
    
    boolean existsByUid(UUID uid);
    
    boolean existsByBidAndUid(int bid, UUID uid);
    
}
