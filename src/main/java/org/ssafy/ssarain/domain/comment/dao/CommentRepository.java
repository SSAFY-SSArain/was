package org.ssafy.ssarain.domain.comment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.ssarain.domain.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
