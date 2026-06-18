package org.ssafy.ssarain.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.comment.dao.CommentRepository;
import org.ssafy.ssarain.domain.comment.dto.CommentCreateDto;
import org.ssafy.ssarain.domain.comment.dto.CommentDetailDto;
import org.ssafy.ssarain.domain.comment.dto.CommentUpdateDto;
import org.ssafy.ssarain.domain.comment.model.Comment;
import org.ssafy.ssarain.domain.node.dao.NodeRepository;
import org.ssafy.ssarain.domain.node.model.Node;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.model.UserRole;
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

        List<Comment> comments = commentRepository.findByNode_NidOrderByCreatedAtAsc(nid);

        return comments.stream()
                .map(CommentDetailDto::from)
                .toList();
    }

    @Transactional
    public CommentDetailDto updateComment(CommentUpdateDto commentUpdateDto, int cid, UUID uid) {

        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

        if(comment.isDeleted()) {
            throw new GlobalException(ErrorCode.COMMENT_NOT_FOUND);
        }

        validateCommentOwner(comment, uid);

        comment.updateContent(commentUpdateDto.content());

        return CommentDetailDto.from(comment);
    }

    @Transactional
    public void deleteComment(int cid, UUID uid, UserRole role) {

        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

        if(comment.isDeleted()) {
            throw new GlobalException(ErrorCode.COMMENT_NOT_FOUND);
        }

        validateCommentOwnerOrAdmin(comment, uid, role);

        comment.delete();
    }


    /*
        Util Method
     */

    private Comment getParentComment(Integer pid, Node node) {
        if(pid == null) {
            return null;
        }

        return commentRepository.findByCidAndNode_Nid(pid, node.getNid())
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateCommentOwner(Comment comment, UUID uid) {
        if(!comment.getUser().getUid().equals(uid)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

    private void validateCommentOwnerOrAdmin(Comment comment, UUID uid, UserRole role) {
        if(role == UserRole.ADMIN) {
            return;
        }

        validateCommentOwner(comment, uid);
    }

}
