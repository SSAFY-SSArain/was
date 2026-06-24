package org.ssafy.ssarain.domain.brain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.brain.model.MergeBrain;
import org.ssafy.ssarain.domain.brain.model.MergeBrain.MergeBrainId;

public interface MergeBrainRepository extends JpaRepository<MergeBrain, MergeBrainId> {

}
