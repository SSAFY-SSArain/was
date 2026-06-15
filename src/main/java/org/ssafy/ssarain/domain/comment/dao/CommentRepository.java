package org.ssafy.ssarain.domain.comment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByNode_NidOrderByCreatedAtAsc(Integer nodeNid);

    Optional<Comment> findByCidAndNode_Nid(Integer cid, Integer nodeNid);
}
