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

    List<Comment> findByNeuron_NidOrderByCreatedAtAsc(Integer neuronNid);

    Optional<Comment> findByCidAndNeuron_Nid(Integer cid, Integer neuronNid);

    boolean existsByCidAndUser_uid(Integer cid, UUID userUid);
}
