package org.ssafy.ssarain.domain.brain.dto.response;

import java.util.List;

public record BrainUserListDto(
        List<BrainUserInfoDto> users
) {

    public static BrainUserListDto from(List<BrainUserInfoDto> users) {
        return new BrainUserListDto(users);
    }
}
