package org.ssafy.ssarain.domain.brain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ssafy.ssarain.domain.brain.dao.BrainMemberRepository;
import org.ssafy.ssarain.domain.brain.model.BrainMemberRole;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrainMemberService {

    private final BrainMemberRepository brainMemberRepository;

    public boolean isBrainMemberByBtid(UUID uid, int bid) {
        return brainMemberRepository.existsByUidAndBtid(uid, bid);
    }
}
