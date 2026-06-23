package org.ssafy.ssarain.domain.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record UserSearchReq(
        @Schema(example = "홍길동")
        @Size(max = 100)
        String keyword,

        @Schema(example = "0")
        Integer page,

        @Schema(example = "10")
        Integer size
) {

    public String searchKeyword() {
        return keyword == null ? "" : keyword.trim();
    }

    public Pageable pageable() {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;
        return PageRequest.of(p, s);
    }
}
