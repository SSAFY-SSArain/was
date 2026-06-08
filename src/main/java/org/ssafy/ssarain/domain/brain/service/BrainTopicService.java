package org.ssafy.ssarain.domain.brain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainTopicRepository;
import org.ssafy.ssarain.domain.brain.dto.response.BrainDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainTopicDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainTopicInfoDto;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.node.dto.NodeInfoDto;
import org.ssafy.ssarain.domain.node.service.NodeService;
import org.ssafy.ssarain.domain.topic.dao.TopicRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainTopicService {

    private final BrainRepository brainRepository;
    private final TopicRepository topicRepository;
    private final BrainTopicRepository brainTopicRepository;
    private final NodeService nodeService;

    @Transactional
    public void registerTopic(int bid, int tid) {
        validateBid(bid);
        validateTid(tid);
        
        brainTopicRepository.addTopicWithAncestors(bid, tid);
    }

    @Transactional(readOnly = true)
    public BrainDetailDto getBrainTopics(int bid) {
        Brain brain = findBrain(bid);
        List<BrainTopicInfoDto> topics = brainTopicRepository.findByBid(bid)
                .stream()
                .map(BrainTopicInfoDto::from)
                .toList();

        return BrainDetailDto.from(brain, topics);
    }

    @Transactional(readOnly = true)
    public BrainTopicDetailDto getBrainTopicDetail(int bid, int tid) {
        BrainTopic brainTopic = findBrainTopic(bid, tid);
        List<NodeInfoDto> nodes = nodeService.findByBrainTopicId(brainTopic.getBtid());

        return BrainTopicDetailDto.from(brainTopic, nodes);
    }
    
    /*
        Util Method
     */
    
    private void validateBid(int bid) {
        if (!brainRepository.existsById(bid)) {
            throw new GlobalException(ErrorCode.BRAIN_TOPIC_NOT_FOUND);
        }
    }
    
    private void validateTid(int tid) {
        if (!topicRepository.existsById(tid)) {
            throw new GlobalException(ErrorCode.BRAIN_TOPIC_NOT_FOUND);
        }
    }

    private Brain findBrain(int bid) {
        return brainRepository.findById(bid)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_NOT_FOUND));
    }
    
    private BrainTopic findBrainTopic(int bid, int tid) {
        return brainTopicRepository.findByBidAndTid(bid, tid)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_TOPIC_NOT_FOUND));
    }
}
