package org.ssafy.ssarain.domain.comment.dto;

import org.ssafy.ssarain.domain.comment.model.Comment;

import java.time.LocalDateTime;

public record CommentDetailDto(

        Integer cid,

        Integer pid,

        String author,

        String content,

        LocalDateTime createdAt
) {

    public static CommentDetailDto from(Comment comment, String author) {
        return new CommentDetailDto(
                comment.getCid(),
                comment.getPid(),
                author,
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
