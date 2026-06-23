package org.ssafy.ssarain.domain.brain.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.common.security.service.BrainAuthService;
import org.ssafy.ssarain.domain.brain.dto.request.BrainCreateDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainSearchDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainUpdateDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainFoundDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainInfoDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainListDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainNameVaildationDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainPageDto;
import org.ssafy.ssarain.domain.brain.service.BrainService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brains")
public class BrainController {
	private final BrainAuthService brainAuthService;
    private final BrainService brainService;
    private final BrainAuthService brainAuthService;
    
    @GetMapping("/me")
    @Operation(summary = "B04: 내가 속한 Brain 조회")
    public ResponseEntity<BaseResponse<BrainListDto<BrainInfoDto>>> getMyBrains(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(SuccessCode.BRAIN_INFO_SUCCESS, brainService.getBrainInfos(userDetails.getUserId()));
    }
    
    @GetMapping
    @Operation(summary = "B05: Brain 검색")
    public ResponseEntity<BaseResponse<BrainPageDto<BrainFoundDto>>> searchBrain(
            @Valid @ModelAttribute BrainSearchDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        return BaseResponse.success(
                SuccessCode.BRAIN_INFO_SUCCESS,
                brainService.searchBrain(dto, userDetails == null ? null : userDetails.getUserId())
        );
    }
    
    @PostMapping
    @Operation(summary = "B06: Brain 생성")
    public ResponseEntity<BaseResponse<BrainDetailDto>> createBrain(
            @Valid @RequestBody BrainCreateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(SuccessCode.BRAIN_CREATED_SUCCESS, brainService.createBrain(dto, userDetails.getUserId()));
    }

    @DeleteMapping("/{bid}")
    @Operation(summary = "B08: Brain 삭제")
    public ResponseEntity<BaseResponse<Void>> deleteBrain(
            @PathVariable int bid,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_ADMIN);
        brainService.deleteBrain(bid);
        return BaseResponse.success(SuccessCode.BRAIN_DELETE_SUCCESS);
    }
    
    @PatchMapping("/{bid}")
    @Operation(summary = "B07: Brain 정보 수정")
    public ResponseEntity<BaseResponse<BrainInfoDto>> updateBrain(
    		@PathVariable int bid,
    		@Valid @RequestBody BrainUpdateDto dto,
    		@AuthenticationPrincipal CustomUserDetails userDetails) {
    	brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
    	return BaseResponse.success(SuccessCode.BRAIN_UPDATE_SUCCESS, brainService.updateBrain(bid, dto));
    }
    
    @GetMapping("/check-name")
    @Operation(summary = "B13: Brain 명 중복 확인")
    public ResponseEntity<BaseResponse<BrainNameVaildationDto>> checkBrainName(
            @RequestParam @NotBlank String name) {
        return BaseResponse.success(SuccessCode.BRAIN_NAME_VALIDATION_SUCCESS, brainService.checkBrainName(name));
    }
    
    @GetMapping("/{bid}")
    @Operation(summary = "B18: Brain 편집 정보 조회")
    public ResponseEntity<BaseResponse<BrainInfoDto>> getBrainInfo(
    		@PathVariable int bid,
    		@AuthenticationPrincipal CustomUserDetails userDetails) {
    	brainAuthService.authorizeBrainRoleOf(userDetails, bid, BrainAuthService.BRAIN_MANAGER);
    	return BaseResponse.success(SuccessCode.BRAIN_INFO_SUCCESS, brainService.getBrainInfo(bid));
    }
}
