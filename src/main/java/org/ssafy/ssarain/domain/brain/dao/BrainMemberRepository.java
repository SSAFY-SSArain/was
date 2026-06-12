package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.brain.model.BrainMember;

public interface BrainMemberRepository extends JpaRepository<BrainMember, BrainMember.BrainMemberId> {
    
    List<BrainMember> findByBmid_Uid(UUID uid);

    boolean existsByBmid_BidAndBmid_Uid(int bid, UUID uid);
    
}
