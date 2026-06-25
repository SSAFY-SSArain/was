package org.ssafy.ssarain.domain.brain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.brain.model.MergeBrain;
import org.ssafy.ssarain.domain.brain.model.MergeBrain.MergeBrainId;

public interface MergeBrainRepository extends JpaRepository<MergeBrain, MergeBrainId> {

    @Query("SELECT mb.mbid.memberid FROM MergeBrain mb WHERE mb.mbid.mainid = :bid")
    List<Integer> findMemberBrainIdsByMainId(int bid);
}
