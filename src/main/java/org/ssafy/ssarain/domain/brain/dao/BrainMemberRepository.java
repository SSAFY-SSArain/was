package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.brain.model.BrainMember;

public interface BrainMemberRepository extends JpaRepository<BrainMember, BrainMember.BrainMemberId> {
    
    List<BrainMember> findByBmid_Uid(UUID uid);
    
    @Query("SELECT (0 < count(*)) FROM BrainMember m WHERE m.bmid.uid = :uid AND role = 'ADMIN'")
    boolean isAnyBrainAdmin(UUID uid);
    
    @Query("SELECT (0 < count(*)) FROM BrainMember m WHERE m.bmid.uid = :uid AND m.bmid.bid = :bid AND role = 'ADMIN'")
    boolean isBrainAdminOf(UUID uid, int bid);
    
}
