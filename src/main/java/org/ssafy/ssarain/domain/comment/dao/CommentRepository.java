package org.ssafy.ssarain.domain.comment.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.ssarain.domain.comment.model.Comment;
import org.ssafy.ssarain.domain.user.dto.UserActivityCommentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    @Query("SELECT count(*) FROM Comment c WHERE c.user.uid = :uid")
    int countByUid(UUID uid);

    List<Comment> findByNeuron_NidOrderByCreatedAtAsc(Integer neuronNid);

    Optional<Comment> findByCidAndNeuron_Nid(Integer cid, Integer neuronNid);

    boolean existsByCidAndUser_uid(Integer cid, UUID userUid);

    @Query(value = """
            SELECT new org.ssafy.ssarain.domain.user.dto.UserActivityCommentDto(
                bt.bid,
                bt.tid,
                n.nid,
                c.cid,
                c.content,
                c.createdAt
            )
            FROM Comment c
            JOIN c.neuron n
            JOIN n.brainTopic bt
            WHERE c.user.uid = :uid
              AND c.deletedAt IS NULL
            ORDER BY c.createdAt DESC
            """,
            countQuery = """
            SELECT COUNT(c)
            FROM Comment c
            WHERE c.user.uid = :uid
              AND c.deletedAt IS NULL
            """)
    Page<UserActivityCommentDto> findWrittenCommentsByUid(UUID uid, Pageable pageable);
}
