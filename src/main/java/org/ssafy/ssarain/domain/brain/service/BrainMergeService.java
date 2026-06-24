package org.ssafy.ssarain.domain.brain.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.util.BatchProcessor;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainTopicRepository;
import org.ssafy.ssarain.domain.brain.dao.MergeBrainRepository;
import org.ssafy.ssarain.domain.brain.dto.request.BrainMergeDto;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.brain.model.JoinPolicy;
import org.ssafy.ssarain.domain.brain.model.MergeBrain;
import org.ssafy.ssarain.domain.topic.dao.TopicRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainMergeService {

    private final TopicRepository      topicRepository;
    private final BrainRepository      brainRepository;
    private final BrainTopicRepository brainTopicRepository;
    private final MergeBrainRepository mergeBrainRepository;
    
    @Transactional
    public void mergeBrains(BrainMergeDto dto) {
        validateMergeRequest(dto);
        
        Brain brain = createBrain(dto.name(), dto.description(), dto.brains());
        copyAllTopics(brain, dto.brains());
        
        // 뉴런, 댓글, 퀴즈 등은 복사하지 않으며, 조회시 기존 정보를 조인하여 반환합니다.
    }
    
    /*
        Util Method
     */
    
    private Brain createBrain(String name, String description, List<Integer> memberBrains) {
        Brain brain = Brain.mergedOf(name, description, JoinPolicy.PROTECTED);
        brainRepository.save(brain);
        
        List<MergeBrain> mergeBrains = memberBrains.stream()
                .map(bid -> MergeBrain.of(brain, brainRepository.getReferenceById(bid)))
                .toList();
        mergeBrainRepository.saveAll(mergeBrains);
        
        return brain;
    }
    
    private void copyAllTopics(Brain toBrain, List<Integer> fromBids) {
        List<Integer> allTopics = getDistinctTidsInBatches(fromBids);
        addTopicsInBatches(toBrain, allTopics);
    }
    
    private List<Integer> getDistinctTidsInBatches(List<Integer> bids) {
        Set<Integer> tids = new HashSet<Integer>();
        BatchProcessor.process(bids, batch -> {
            tids.addAll(brainTopicRepository.findDistinctTidByBidIn(batch));
        });
        return new ArrayList<>(tids);
    }
    
    private void addTopicsInBatches(Brain brain, List<Integer> tids) {
        BatchProcessor.process(tids, batch -> {
            List<BrainTopic> a = batch.stream()
                    .map(tid -> BrainTopic.of(brain, topicRepository.getReferenceById(tid)))
                    .toList();
            brainTopicRepository.saveAll(a);
        });
    }
    
    private void validateMergeRequest(BrainMergeDto dto) {
        validateDuplicateName(dto.name());
        validateBrainIdInBatches(dto.brains());
    }
    
    private void validateDuplicateName(String name) {
        if (brainRepository.existsByName(name)) {
            throw new GlobalException(ErrorCode.BRAIN_NAME_DUPLICATED);
        }
    }
    
    private void validateBrainIdInBatches(List<Integer> bids) {
        BatchProcessor.process(bids, batch -> {
            if (batch.size() != brainRepository.countBybidIn(batch)) {
                throw new GlobalException(ErrorCode.BRAIN_NOT_FOUND);
            }
        });
    }
}
