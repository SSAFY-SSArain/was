package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;

public interface BrainMemberRepository extends JpaRepository<BrainMember, BrainMember.BrainMemberId> {
    
    List<BrainMember> findByBmid_Uid(UUID uid);
        
    boolean existsByBmidUidAndRole(UUID uid, BrainMemberRole role);

    boolean existsByBmidUidAndBmidBidAndRole(UUID uid, int bid, BrainMemberRole role);

    boolean existsByBmidUidAndBmidBid(UUID bmidUid, int bmidBid);
}
