package org.ssafy.ssarain.domain.brain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainTopicRepository;
import org.ssafy.ssarain.domain.brain.dto.request.TopicIdListDto;
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
    
    private static final int VALIDATION_BATCH_SIZE = 120;

    private final BrainRepository brainRepository;
    private final TopicRepository topicRepository;
    private final BrainTopicRepository brainTopicRepository;
    private final NodeService nodeService;

    @Transactional
    public void registerTopic(int bid, TopicIdListDto dto) {
        validateBid(bid);
        
        List<Integer> tids = getValidTids(dto);
        brainTopicRepository.addTopicWithAncestors(bid, tids);
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
            throw new GlobalException(ErrorCode.BRAIN_NOT_FOUND);
        }
    }
    
    private List<Integer> getValidTids(TopicIdListDto dto) {
        List<Integer> tids = dto.topics().stream()
                .distinct()
                .collect(Collectors.toList());
        
        validateTidsInBatches(tids);
        return tids;
    }
    
    private void validateTidsInBatches(List<Integer> tids) {
        int pageCount = 1 + (tids.size() - 1) / VALIDATION_BATCH_SIZE;
        for (int page = 0; page < pageCount; page++) {
            List<Integer> batch = tids.subList(
                    page * VALIDATION_BATCH_SIZE,
                    Math.min((page + 1) * VALIDATION_BATCH_SIZE, tids.size()));
            
            if (batch.size() != topicRepository.countByTidIn(batch)) {
                throw new GlobalException(ErrorCode.TOPIC_NOT_FOUND);
            }
        }
    }

    private Brain findBrain(int bid) {
        return brainRepository.findById(bid)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_NOT_FOUND));
    }
    
    private BrainTopic findBrainTopic(int bid, int tid) {
        return brainTopicRepository.findByBidAndTid(bid, tid)
                .orElseThrow(() -> new GlobalException(ErrorCode.BRAIN_OR_TOPIC_NOT_FOUND));
    }
}
