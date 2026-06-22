package org.ssafy.ssarain.domain.neuron.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.neuron.model.Neuron;
import org.ssafy.ssarain.domain.user.dto.UserActivityNeuronDto;

import java.util.List;
import java.util.UUID;

public interface NeuronRepository extends JpaRepository<Neuron, Integer> {
    @Query("SELECT count(*) FROM Neuron n WHERE n.user.uid = :uid")
    int countByUid(UUID uid);
    List<Neuron> findByBrainTopic_Btid(Integer btid);

    boolean existsByNidAndUser_Uid(Integer nid, UUID userUid);

    @Query(value = """
            SELECT new org.ssafy.ssarain.domain.user.dto.UserActivityNeuronDto(
                bt.bid,
                bt.tid,
                n.nid,
                n.title,
                n.createdAt
            )
            FROM Neuron n
            JOIN n.brainTopic bt
            WHERE n.user.uid = :uid
            ORDER BY n.createdAt DESC
            """,
            countQuery = """
            SELECT COUNT(n)
            FROM Neuron n
            WHERE n.user.uid = :uid
            """)
    Page<UserActivityNeuronDto> findWrittenNeuronsByUid(UUID uid, Pageable pageable);
}
