package org.ssafy.ssarain.domain.brain.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ssafy.ssarain.common.response.BaseResponse;
import org.ssafy.ssarain.common.response.SuccessCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.brain.dto.request.BrainCreateDto;
import org.ssafy.ssarain.domain.brain.dto.request.BrainSearchDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainDetailDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainFoundDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainInfoDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainListDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainNameVaildationDto;
import org.ssafy.ssarain.domain.brain.dto.response.BrainPageDto;
import org.ssafy.ssarain.domain.brain.service.BrainService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brains")
public class BrainController {
    private final BrainService brainService;
    
    @GetMapping("/me")
    @Operation(summary = "B04: 내가 속한 Brain 조회")
    public ResponseEntity<BaseResponse<BrainListDto<BrainInfoDto>>> getMyBrains(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(SuccessCode.BRAIN_INFO_SUCCESS, brainService.getBrainInfos(userDetails.getUserId()));
    }
    
    @GetMapping
    @Operation(summary = "B05: Brain 검색")
    public ResponseEntity<BaseResponse<BrainPageDto<BrainFoundDto>>> searchBrain(
            @Valid @ModelAttribute BrainSearchDto dto) {
        return BaseResponse.success(SuccessCode.BRAIN_INFO_SUCCESS, brainService.searchBrain(dto));
    }
    
    @PostMapping
    @Operation(summary = "B06: Brain 생성")
    public ResponseEntity<BaseResponse<BrainDetailDto>> createBrain(
            @Valid @RequestBody BrainCreateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(SuccessCode.BRAIN_CREATED_SUCCESS, brainService.createBrain(dto, userDetails.getUserId()));
    }
    
    @GetMapping("/check-name")
    @Operation(summary = "B13: Brain 명 중복 확인")
    public ResponseEntity<BaseResponse<BrainNameVaildationDto>> checkBrainName(
            @RequestParam String name) {
        return BaseResponse.success(SuccessCode.BRAIN_NAME_VALIDATION_SUCCESS, brainService.checkBrainName(name));
    }
}
