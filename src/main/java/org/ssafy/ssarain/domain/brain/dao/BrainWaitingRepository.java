package org.ssafy.ssarain.domain.brain.dao;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.brain.model.BrainWaiting;

public interface BrainWaitingRepository extends JpaRepository<BrainWaiting, BrainWaiting.BrainWaitingId> {

    Optional<BrainWaiting> findByBmidBidAndBmidUid(int bid, UUID uid);
    
    boolean existsByBmidBidAndBmidUid(int bid, UUID uid);
    
    Page<BrainWaiting> findByBmidBid(int bid, Pageable pageable);
}
