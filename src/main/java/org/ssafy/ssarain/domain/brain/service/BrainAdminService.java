package org.ssafy.ssarain.domain.brain.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.ssafy.ssarain.domain.brain.dao.BrainManagerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainAdminService {
    
    private final BrainManagerRepository brainManagerRepository;
    
    public boolean isAnyBrainAdmin(UUID uid) {
        return brainManagerRepository.existsByUid(uid);
    }
    
    public boolean isBrainAdminOf(UUID uid, int bid) {
        return brainManagerRepository.existsByBidAndUid(bid, uid);
    }
}
