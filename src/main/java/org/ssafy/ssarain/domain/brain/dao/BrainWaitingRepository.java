package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.brain.model.BrainWaiting;

public interface BrainWaitingRepository extends JpaRepository<BrainWaiting, BrainWaiting.BrainWaitingId> {

    Optional<BrainWaiting> findByBmidBidAndBmidUid(int bid, UUID uid);
    
    boolean existsByBmidBidAndBmidUid(int bid, UUID uid);
    
    List<BrainWaiting> findByBmidBid(int bid);
}
