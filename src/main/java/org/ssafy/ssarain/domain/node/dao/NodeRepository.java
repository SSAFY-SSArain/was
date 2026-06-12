package org.ssafy.ssarain.domain.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.node.model.Node;

public interface NodeRepository extends JpaRepository<Node, Integer> {
}