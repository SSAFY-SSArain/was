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
import org.ssafy.ssarain.domain.neuron.dao.NeuronRepository;
import org.ssafy.ssarain.domain.neuron.model.Neuron;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NeuronRepository    neuronRepository;
    private final UserService       userService;

    @Transactional
    public CommentDetailDto createComment(CommentCreateDto commentCreateDto, UUID uid) {

        Neuron neuron      = neuronRepository.findById(commentCreateDto.nid())
                                        .orElseThrow(() -> new GlobalException(ErrorCode.NEURON_NOT_FOUND));
        Comment parent = getParentComment(commentCreateDto.pid(), neuron);
        User user      = userService.getUserByUserId(uid);

        Comment comment = Comment.of(
                neuron,
                parent,
                user,
                commentCreateDto.content()
        );

        return CommentDetailDto.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentDetailDto> getCommentsByNid(Integer nid) {

        List<Comment> comments = commentRepository.findByNeuron_NidOrderByCreatedAtAsc(nid);

        return comments.stream()
                .map(CommentDetailDto::from)
                .toList();
    }

    @Transactional
    public CommentDetailDto updateComment(CommentUpdateDto commentUpdateDto, int cid) {

        Comment comment = getActiveCommentById(cid);

        comment.updateContent(commentUpdateDto.content());

        return CommentDetailDto.from(comment);
    }

    @Transactional
    public void deleteComment(int cid) {

        Comment comment = getActiveCommentById(cid);

        comment.delete();
    }


    /*
        Util Method
     */

    private Comment getActiveCommentById(Integer cid) {

        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));

        if(comment.isDeleted()) {
            throw new GlobalException(ErrorCode.COMMENT_NOT_FOUND);
        }

        return comment;
    }

    private Comment getParentComment(Integer pid, Neuron neuron) {
        if(pid == null) {
            return null;
        }

        return commentRepository.findByCidAndNeuron_Nid(pid, neuron.getNid())
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));
    }

}
