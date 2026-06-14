package org.ssafy.ssarain.domain.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.node.model.Node;

import java.util.List;

public interface NodeRepository extends JpaRepository<Node, Integer> {
    List<Node> findByBrainTopic_Btid(int btid);
}