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

        validateCreateComment(commentCreateDto);

        Comment comment = commentCreateDto.toEntity(uid);
        User user = userService.getUserByUserId(uid);

        return CommentDetailDto.from(commentRepository.save(comment), user.getName());
    }


    /*
        Util Method
     */

    private void validateCreateComment(CommentCreateDto commentCreateDto) {
        nodeService.validateExists(commentCreateDto.nid());
        if(commentCreateDto.pid() != null) {
            validateParentCommentExists(commentCreateDto.pid());
        }
    }

    private void validateParentCommentExists(Integer pid) {
        if(!commentRepository.existsById(pid)) {
            throw new GlobalException(ErrorCode.COMMENT_NOT_FOUND);
        }
    }

}
