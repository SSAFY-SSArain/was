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
import org.ssafy.ssarain.domain.node.dao.NodeRepository;
import org.ssafy.ssarain.domain.node.model.Node;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NodeRepository    nodeRepository;
    private final UserService       userService;

    @Transactional
    public CommentDetailDto createComment(CommentCreateDto commentCreateDto, UUID uid) {

        Node node      = nodeRepository.findById(commentCreateDto.nid())
                                        .orElseThrow(() -> new GlobalException(ErrorCode.NODE_NOT_FOUND));
        Comment parent = getParentComment(commentCreateDto.pid(), node);
        User user      = userService.getUserByUserId(uid);

        Comment comment = Comment.of(
                node,
                parent,
                user,
                commentCreateDto.content()
        );

        return CommentDetailDto.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentDetailDto> getCommentsByNid(Integer nid) {

        List<Comment> comments = commentRepository.findByNode_Nid(nid);

        return comments.stream()
                .map(CommentDetailDto::from)
                .toList();
    }


    /*
        Util Method
     */

    private Comment getParentComment(Integer pid, Node node) {
        if(pid == null) {
            return null;
        }

        Comment parent = commentRepository.findById(pid)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

        // 부모 node와 자식 node가 다른 경우
        if(!parent.getNode().getNid().equals(node.getNid())) {
            throw new GlobalException(ErrorCode.COMMENT_NOT_FOUND);
        }

        return parent;
    }

}
