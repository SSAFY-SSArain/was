package org.ssafy.ssarain.domain.neuron.service;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.brain.dao.BrainTopicRepository;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.comment.dto.CommentDetailDto;
import org.ssafy.ssarain.domain.comment.service.CommentService;
import org.ssafy.ssarain.domain.neuron.dao.NeuronRepository;
import org.ssafy.ssarain.domain.neuron.dto.*;
import org.ssafy.ssarain.domain.neuron.model.Neuron;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.service.UserService;

@Service
@RequiredArgsConstructor
public class NeuronService {

    private final UserService          userService;
    private final NeuronRepository       neuronRepository;
    private final BrainTopicRepository brainTopicRepository;
    private final CommentService       commentService;


    @Transactional(readOnly = true)
    public NeuronPreviewListDto getNeuronPreview(Integer btid) {

        List<Neuron> neurons = neuronRepository.findByBrainTopic_BtidAndDeletedAtIsNull(btid);
        List<NeuronPreviewDto> neuronPreviewList = neurons.stream()
                                                    .map(NeuronPreviewDto::from)
                                                    .toList();

        return NeuronPreviewListDto.from(neuronPreviewList);
    }

    @Transactional(readOnly = true)
    public NeuronDetailDto getNeuron(Integer nid) {

        Neuron neuron = neuronRepository.findByNidAndDeletedAtIsNull(nid)
                .orElseThrow(() -> new GlobalException(ErrorCode.NEURON_NOT_FOUND));

        List<CommentDetailDto> comments = commentService.getCommentsByNid(nid);

        return NeuronDetailDto.from(neuron, comments);
    }

    @Transactional
    public NeuronDetailDto createNeuron(NeuronCreateDto neuronCreateDto, UUID uid) {

        // 권한 검증에서 brainTopic 존재 검증
        BrainTopic brainTopic = brainTopicRepository.getReferenceById(neuronCreateDto.btid());

        User user = userService.getUserByUserId(uid);

        Neuron neuron = Neuron.of(brainTopic, user, neuronCreateDto.title(), neuronCreateDto.content());

        return NeuronDetailDto.from(neuronRepository.save(neuron));
    }

    @Transactional(readOnly = true)
    public List<NeuronInfoDto> findByBrainTopicId(Integer brainTopicId) {

        List<Neuron> neurons = neuronRepository.findByBrainTopic_BtidAndDeletedAtIsNull(brainTopicId);

        return neurons.stream()
                .map(NeuronInfoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findTitlesByBrainTopicId(Integer brainTopicId) {

        List<Neuron> neurons = neuronRepository.findByBrainTopic_BtidAndDeletedAtIsNull(brainTopicId);

        return neurons.stream()
                .map(Neuron::getTitle)
                .toList();
    }

    @Transactional
    public void deleteNeuron(int nid) {

        Neuron neuron = neuronRepository.findByNidAndDeletedAtIsNull(nid)
                .orElseThrow(() -> new GlobalException(ErrorCode.NEURON_NOT_FOUND));

        neuron.delete();
    }
}
