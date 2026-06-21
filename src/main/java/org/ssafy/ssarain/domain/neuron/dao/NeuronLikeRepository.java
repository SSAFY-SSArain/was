package org.ssafy.ssarain.domain.neuron.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.neuron.model.NeuronLike;

import java.util.UUID;

public interface NeuronLikeRepository extends JpaRepository<NeuronLike, NeuronLike.NeuronLikeId> {

    int countByNeuron_Nid(Integer neuronNid);

    boolean existsByUser_UidAndNeuron_Nid(UUID userUid, Integer neuronNid);

    void deleteNeuronLikeByUser_UidAndNeuron_Nid(UUID userUid, Integer neuronNid);
}