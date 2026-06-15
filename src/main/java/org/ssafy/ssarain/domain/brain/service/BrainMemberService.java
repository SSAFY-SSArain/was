package org.ssafy.ssarain.domain.brain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrainMemberService {

    private final BrainMemberRepository brainMemberRepository;

    public boolean isBrainMember(UUID uid, int bid) {
        return brainMemberRepository.existsByBmidUidAndBmidBid(uid, bid);
    }
}
