package org.ssafy.ssarain.domain.neuron.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.neuron.model.NeuronLike;
import org.ssafy.ssarain.domain.user.dto.UserActivityNeuronDto;

import java.util.UUID;

public interface NeuronLikeRepository extends JpaRepository<NeuronLike, NeuronLike.NeuronLikeId> {

    int countByNeuron_Nid(Integer neuronNid);

    boolean existsByUser_UidAndNeuron_Nid(UUID userUid, Integer neuronNid);

    void deleteNeuronLikeByUser_UidAndNeuron_Nid(UUID userUid, Integer neuronNid);

    @Query(value = """
            SELECT new org.ssafy.ssarain.domain.user.dto.UserActivityNeuronDto(
                bt.bid,
                bt.tid,
                n.nid,
                n.title,
                nl.createdAt
            )
            FROM NeuronLike nl
            JOIN nl.neuron n
            JOIN n.brainTopic bt
            WHERE nl.user.uid = :uid
            ORDER BY nl.createdAt DESC
            """,
            countQuery = """
            SELECT COUNT(nl)
            FROM NeuronLike nl
            WHERE nl.user.uid = :uid
            """)
    Page<UserActivityNeuronDto> findRecommendedNeuronsByUid(UUID uid, Pageable pageable);
}
