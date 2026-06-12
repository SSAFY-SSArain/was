package org.ssafy.ssarain.domain.node.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.node.dto.NodeCreateDto;
import org.ssafy.ssarain.domain.node.dto.NodeDetailDto;
import org.ssafy.ssarain.domain.node.service.NodeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/nodes")
public class NodeController {

    private final NodeService nodeService;

    @PostMapping
    public ResponseEntity<BaseResponse<NodeDetailDto>> createNode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NodeCreateDto nodeCreateDto
    ) {

            NodeDetailDto nodeDetailDto = nodeService.createNode(nodeCreateDto, userDetails.getUserId());

            return BaseResponse.success(SuccessCode.NODE_CREATE_SUCCESS, nodeDetailDto);
    }

}
