package org.ssafy.ssarain.domain.brain.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrainAdminService {
    
    private final BrainMemberRepository brainMemberRepository;
    
    public boolean isAnyBrainAdmin(UUID uid) {
        return brainMemberRepository.isAnyBrainAdmin(uid);
    }
    
    public boolean isBrainAdminOf(UUID uid, int bid) {
        return brainMemberRepository.isBrainAdminOf(uid, bid);
    }
}
