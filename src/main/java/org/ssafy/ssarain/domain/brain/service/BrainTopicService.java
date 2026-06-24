package org.ssafy.ssarain.domain.brain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.util.BatchProcessor;
import org.ssafy.ssarain.domain.brain.dao.BrainRepository;
import org.ssafy.ssarain.domain.brain.dao.BrainTopicRepository;
import org.ssafy.ssarain.domain.brain.dto.request.TopicDeleteListDto;
import org.ssafy.ssarain.domain.brain.dto.request.TopicIdListDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainTopicDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainTopicInfoDto;
import org.ssafy.ssarain.domain.brain.model.Brain;
import org.ssafy.ssarain.domain.brain.model.BrainTopic;
import org.ssafy.ssarain.domain.neuron.dao.NeuronRepository;
import org.ssafy.ssarain.domain.neuron.dto.NeuronInfoDto;
import org.ssafy.ssarain.domain.neuron.service.NeuronService;
import org.ssafy.ssarain.domain.topic.dao.TopicRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainTopicService {

    private final BrainRepository      brainRepository;
    private final TopicRepository      topicRepository;
    private final BrainTopicRepository brainTopicRepository;
    private final NeuronRepository     neuronRepository;
    private final NeuronService        neuronService;

    @Transactional
    public void registerTopic(int bid, TopicIdListDto dto) {
        validateBid(bid);
        
        List<Integer> tids = getValidTids(dto);
        brainTopicRepository.addTopicWithAncestors(bid, tids);
    }

    @Transactional(readOnly = true)
    public BrainDetailDto getBrainTopics(int bid, Integer tid, int depth) {
        Brain brain = findBrain(bid);
        if (tid != null) {
        	validateBrainTopic(bid, tid);
        }
        
        List<BrainTopicInfoDto> topics = brainTopicRepository.findByPidAndBid(bid, tid, depth)
                .stream()
                .map(BrainTopicInfoDto::from)
                .toList();

        return BrainDetailDto.from(brain, topics);
    }

    @Transactional(readOnly = true)
    public BrainTopicDetailDto getBrainTopicDetail(int bid, int tid) {
        BrainTopic brainTopic = findBrainTopic(bid, tid);
        List<NeuronInfoDto> neurons = neuronService.findByBrainTopicId(brainTopic.getBtid());

        return BrainTopicDetailDto.from(brainTopic, neurons);
    }

    @Transactional
	public void deleteTopic(int bid, TopicDeleteListDto dto) {
		validateBid(bid);
		validateBrainTopicInBatches(bid, dto.topics());
		
		List<Integer> btids = getValidBTidDeleteList(bid, dto.topics());
		
		if (!dto.unsafe()) {
			validateDeletableBrainTopicInBatches(btids);
		}
		brainTopicRepository.deleteAllByIdInBatch(btids);			
	}
    
    public boolean existBrainTopic(int btid) {
        return brainTopicRepository.existsById(btid);
    }
    
    /*
        Util Method
     */
    
    private void validateBid(int bid) {
        if (!brainRepository.existsById(bid)) {
            throw new GlobalException(ErrorCode.BRAIN_NOT_FOUND);
        }
    }
    
    private void validateBrainTopic(int bid, int tid) {
        if (!brainTopicRepository.existsByBidAndTid(bid, tid)) {
        	throw new GlobalException(ErrorCode.BRAIN_OR_TOPIC_NOT_FOUND);
        }
    }
    
    private List<Integer> getValidTids(TopicIdListDto dto) {
    	List<Integer> tids = getDistinctTidList(dto.topics());
    	
        validateTidsInBatches(tids);
        return tids;
    }
    
    private void validateTidsInBatches(List<Integer> tids) {
        BatchProcessor.process(tids, batch -> {
            if (batch.size() != topicRepository.countByTidIn(batch)) {
                throw new GlobalException(ErrorCode.TOPIC_NOT_FOUND);
            } 
        });
    }
    
    private void validateBrainTopicInBatches(int bid, List<Integer> tids) {
    	BatchProcessor.process(tids, batch -> {
            if (batch.size() != brainTopicRepository.countByBidAndTidIn(bid, batch)) {
                throw new GlobalException(ErrorCode.BRAIN_TOPIC_NOT_FOUND);
            } 
        });
    }
    
    private void validateDeletableBrainTopicInBatches(List<Integer> btids) {
    	BatchProcessor.process(btids, batch -> {
            if (0 < neuronRepository.countByBtidIn(batch)) {
                throw new GlobalException(ErrorCode.BRAIN_TOPIC_HAS_NEURON);
            } 
        });
    }
    
    private List<Integer> getValidBTidDeleteList(int bid, List<Integer> deleteTids) {
        List<Integer> tids = getDistinctTidList(deleteTids);
        
        List<Integer> btids = new ArrayList<>();
        BatchProcessor.process(tids, batch -> {
        	List<Integer> batchBtids = brainTopicRepository.findDescendantBtidByBidAndTidIn(bid, batch);
            btids.addAll(batchBtids);
        });
        return btids;
    }
    
    private List<Integer> getDistinctTidList(List<Integer> rawTids) {
    	if (rawTids == null) {
    		return new ArrayList<>();
    	}
    	
    	return rawTids.stream()
		        .distinct()
		        .collect(Collectors.toList());
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
