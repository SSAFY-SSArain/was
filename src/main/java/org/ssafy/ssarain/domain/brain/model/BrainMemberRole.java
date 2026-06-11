package org.ssafy.ssarain.domain.brain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BrainMemberRole {
    USER,
    ADMIN
}
