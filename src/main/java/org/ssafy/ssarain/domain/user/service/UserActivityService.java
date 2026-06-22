package org.ssafy.ssarain.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.domain.comment.dao.CommentRepository;
import org.ssafy.ssarain.domain.neuron.dao.NeuronLikeRepository;
import org.ssafy.ssarain.domain.neuron.dao.NeuronRepository;
import org.ssafy.ssarain.domain.user.dto.UserActivityCommentDto;
import org.ssafy.ssarain.domain.user.dto.UserActivityNeuronDto;
import org.ssafy.ssarain.domain.user.dto.UserActivityPageDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final NeuronRepository     neuronRepository;
    private final CommentRepository    commentRepository;
    private final NeuronLikeRepository neuronLikeRepository;

    @Transactional(readOnly = true)
    public UserActivityPageDto<UserActivityNeuronDto> getWrittenNeurons(UUID uid, Pageable pageable) {
        return UserActivityPageDto.from(neuronRepository.findWrittenNeuronsByUid(uid, pageable));
    }

    @Transactional(readOnly = true)
    public UserActivityPageDto<UserActivityCommentDto> getWrittenComments(UUID uid, Pageable pageable) {
        return UserActivityPageDto.from(commentRepository.findWrittenCommentsByUid(uid, pageable));
    }

    @Transactional(readOnly = true)
    public UserActivityPageDto<UserActivityNeuronDto> getRecommendedNeurons(UUID uid, Pageable pageable) {
        return UserActivityPageDto.from(neuronLikeRepository.findRecommendedNeuronsByUid(uid, pageable));
    }
}
