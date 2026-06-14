package org.ssafy.ssarain.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.comment.dao.CommentRepository;
import org.ssafy.ssarain.domain.comment.dto.CommentCreateDto;
import org.ssafy.ssarain.domain.comment.dto.CommentDetailDto;
import org.ssafy.ssarain.domain.comment.model.Comment;
import org.ssafy.ssarain.domain.node.model.Node;
import org.ssafy.ssarain.domain.node.service.NodeService;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.service.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NodeService       nodeService;
    private final UserService       userService;

    @Transactional
    public CommentDetailDto createComment(CommentCreateDto commentCreateDto, UUID uid) {

        Node node      = nodeService.getNodeById(commentCreateDto.nid());
        Comment parent = getParentComment(commentCreateDto.pid());
        User user      = userService.getUserByUserId(uid);

        Comment comment = Comment.of(
                node,
                parent,
                user,
                commentCreateDto.content()
        );

        return CommentDetailDto.from(commentRepository.save(comment));
    }


    /*
        Util Method
     */

    private Comment getParentComment(Integer pid) {
        if(pid == null) {
            return null;
        }

        return commentRepository.findById(pid)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));
    }

}
