package org.ssafy.ssarain.domain.neuron.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.neuron.model.Neuron;

import java.util.List;
import java.util.UUID;

public interface NeuronRepository extends JpaRepository<Neuron, Integer> {
    @Query("SELECT count(*) FROM Neuron n WHERE n.user.uid = :uid")
    int countByUid(UUID uid);
    List<Neuron> findByBrainTopic_Btid(Integer btid);

    boolean existsByNidAndUser_Uid(Integer nid, UUID userUid);
}
