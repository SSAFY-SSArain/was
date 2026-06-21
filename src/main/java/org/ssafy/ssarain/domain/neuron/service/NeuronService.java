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
import org.ssafy.ssarain.domain.neuron.dao.NeuronLikeRepository;
import org.ssafy.ssarain.domain.neuron.dao.NeuronRepository;
import org.ssafy.ssarain.domain.neuron.dto.*;
import org.ssafy.ssarain.domain.neuron.model.Neuron;
import org.ssafy.ssarain.domain.neuron.model.NeuronLike;
import org.ssafy.ssarain.domain.user.model.User;
import org.ssafy.ssarain.domain.user.service.UserService;

@Service
@RequiredArgsConstructor
public class NeuronService {

    private final UserService          userService;
    private final NeuronRepository     neuronRepository;
    private final NeuronLikeRepository neuronLikeRepository;
    private final BrainTopicRepository brainTopicRepository;
    private final CommentService       commentService;


    @Transactional(readOnly = true)
    public NeuronPreviewListDto getNeuronPreview(Integer btid) {

        List<Neuron> neurons = neuronRepository.findByBrainTopic_Btid(btid);
        List<NeuronPreviewDto> neuronPreviewList = neurons.stream()
                                                    .map(NeuronPreviewDto::from)
                                                    .toList();

        return NeuronPreviewListDto.from(neuronPreviewList);
    }

    @Transactional(readOnly = true)
    public NeuronDetailDto getNeuron(Integer nid, UUID uid) {

        Neuron neuron = neuronRepository.findById(nid)
                .orElseThrow(() -> new GlobalException(ErrorCode.NEURON_NOT_FOUND));

        List<CommentDetailDto> comments = commentService.getCommentsByNid(nid);

        int likeCount = neuronLikeRepository.countByNeuron_Nid(nid);
        boolean liked = uid != null && neuronLikeRepository.existsByUser_UidAndNeuron_Nid(uid,nid);

        return NeuronDetailDto.from(neuron, likeCount, liked, comments);
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

        List<Neuron> neurons = neuronRepository.findByBrainTopic_Btid(brainTopicId);

        return neurons.stream()
                .map(NeuronInfoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findTitlesByBrainTopicId(Integer brainTopicId) {

        List<Neuron> neurons = neuronRepository.findByBrainTopic_Btid(brainTopicId);

        return neurons.stream()
                .map(Neuron::getTitle)
                .toList();
    }

    @Transactional
    public void deleteNeuron(int nid) {

        Neuron neuron = neuronRepository.findById(nid)
                .orElseThrow(() -> new GlobalException(ErrorCode.NEURON_NOT_FOUND));

        neuronRepository.delete(neuron);
    }

    @Transactional
    public NeuronLikeDto likeNeuron(int nid, UUID uid) {

        Neuron neuron = neuronRepository.findById(nid)
                .orElseThrow(() -> new GlobalException(ErrorCode.NEURON_NOT_FOUND));

        boolean alreadyLiked = neuronLikeRepository.existsByUser_UidAndNeuron_Nid(uid,nid);

        if(alreadyLiked) {
            neuronLikeRepository.deleteNeuronLikeByUser_UidAndNeuron_Nid(uid,nid);
        }
        else {
            User user = userService.getUserByUserId(uid);
            neuronLikeRepository.save(NeuronLike.of(user, neuron));
        }

        int likeCount = neuronLikeRepository.countByNeuron_Nid(nid);
        return NeuronLikeDto.from(likeCount, !alreadyLiked);
    }
}
