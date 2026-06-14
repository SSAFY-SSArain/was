package org.ssafy.ssarain.domain.comment.dto;

import org.ssafy.ssarain.domain.comment.model.Comment;

import java.time.LocalDateTime;

public record CommentDetailDto(

        Integer cid,

        Integer pid,

        String writer,

        String content,

        LocalDateTime createdAt
) {

    public static CommentDetailDto from(Comment comment) {
        return new CommentDetailDto(
                comment.getCid(),
                comment.getPid(),
                comment.getUser().getName(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
