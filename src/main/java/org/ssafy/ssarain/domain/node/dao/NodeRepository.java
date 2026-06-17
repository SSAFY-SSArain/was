package org.ssafy.ssarain.domain.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.node.model.Node;

import java.util.List;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<Node, Integer> {
    @Query("SELECT count(*) FROM Node n WHERE n.user.uid = :uid")
    int countByUid(UUID uid);
    List<Node> findByBrainTopic_Btid(Integer btid);
}