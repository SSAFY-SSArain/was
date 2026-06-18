package org.ssafy.ssarain.domain.comment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.comment.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    @Query("SELECT count(*) FROM Comment c WHERE c.user.uid = :uid")
    int countByUid(UUID uid);

    List<Comment> findByNode_NidOrderByCreatedAtAsc(Integer nodeNid);

    Optional<Comment> findByCidAndNode_Nid(Integer cid, Integer nodeNid);
}
