package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.brain.model.BrainWaiting;

public interface BrainWaitingRepository extends JpaRepository<BrainWaiting, BrainWaiting.BrainWaitingId> {

    List<BrainWaiting> findByBmid_Bid(int bid);
}
