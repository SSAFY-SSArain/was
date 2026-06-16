package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;

public interface BrainMemberRepository extends JpaRepository<BrainMember, BrainMember.BrainMemberId> {
    
    List<BrainMember> findByBmid_Uid(UUID uid);
        
    boolean existsByBmidUidAndRole(UUID uid, BrainMemberRole role);

    boolean existsByBmidUidAndBmidBidAndRole(UUID uid, int bid, BrainMemberRole role);
    
    long countAllByBmidIn(List<BrainMember.BrainMemberId> ids);

    @Query("""
            SELECT 0 < count(*)
            FROM BrainMember bm
            INNER JOIN BrainTopic bt ON bm.bmid.bid = bt.bid AND bt.btid = :btid
            WHERE bm.bmid.uid = :uid
            """)
    boolean existsByUidAndBtid(UUID uid, int btid);
}
