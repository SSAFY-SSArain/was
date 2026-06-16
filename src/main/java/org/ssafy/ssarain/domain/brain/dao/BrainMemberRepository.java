package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.brain.model.BrainMember;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;

public interface BrainMemberRepository extends JpaRepository<BrainMember, BrainMember.BrainMemberId> {
    
    List<BrainMember> findByBmid_Uid(UUID uid);
    
    @Query("SELECT bm.role FROM BrainMember bm WHERE (bm.bmid.uid, bm.bmid.bid) = (:uid, :bid)")
    BrainMemberRole findRoleByBmidUidAndBmidBid(UUID uid, int bid);
        
    boolean existsByBmidUidAndRoleIn(UUID uid, List<BrainMemberRole> role);

    boolean existsByBmidUidAndBmidBidAndRoleIn(UUID uid, int bid, List<BrainMemberRole> role);
    
    long countAllByBmidIn(List<BrainMember.BrainMemberId> ids);

    @Query("""
            SELECT 0 < count(*)
            FROM BrainMember bm
            INNER JOIN BrainTopic bt ON bm.bmid.bid = bt.bid AND bt.btid = :btid
            WHERE bm.bmid.uid = :uid
            """)
    boolean existsByUidAndBtid(UUID uid, int btid);
}
