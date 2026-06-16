package org.ssafy.ssarain.domain.node.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.common.security.service.BrainAuthService;
import org.ssafy.ssarain.domain.node.dto.NodeCreateDto;
import org.ssafy.ssarain.domain.node.dto.NodeDetailDto;
import org.ssafy.ssarain.domain.node.dto.NodePreviewListDto;
import org.ssafy.ssarain.domain.node.service.NodeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/nodes")
public class NodeController {

    private final NodeService      nodeService;
    private final BrainAuthService brainAuthService;

    @GetMapping("/preview/{btid}")
    @Operation(summary = "N01: Node 프리 조회")
    public ResponseEntity<BaseResponse<NodePreviewListDto>> getNodePreview(
            @PathVariable Integer btid
    ) {

        NodePreviewListDto nodes = nodeService.getNodePreview(btid);

        return BaseResponse.success(SuccessCode.NODE_INFO_SUCCESS, nodes);
    }

    @GetMapping("/{nid}")
    @Operation(summary = "N02: Node 상세 정보 조회")
    public ResponseEntity<BaseResponse<NodeDetailDto>> getNode(
            @PathVariable Integer nid
    ) {

        NodeDetailDto nodeDetailDto = nodeService.getNode(nid);

        return BaseResponse.success(SuccessCode.NODE_INFO_SUCCESS, nodeDetailDto);
    }

    @PostMapping
    @Operation(summary = "N03: Node 추가")
    public ResponseEntity<BaseResponse<NodeDetailDto>> createNode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid NodeCreateDto nodeCreateDto
    ) {

        brainAuthService.authorizeBrainMemberByBtid(userDetails, nodeCreateDto.btid());
        NodeDetailDto nodeDetailDto = nodeService.createNode(nodeCreateDto, userDetails.getUserId());

        return BaseResponse.success(SuccessCode.NODE_CREATE_SUCCESS, nodeDetailDto);
    }

}
