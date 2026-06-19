package org.ssafy.ssarain.domain.neuron.api;

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
import org.ssafy.ssarain.common.security.service.NeuronAuthService;
import org.ssafy.ssarain.domain.neuron.dto.NeuronCreateDto;
import org.ssafy.ssarain.domain.neuron.dto.NeuronDetailDto;
import org.ssafy.ssarain.domain.neuron.dto.NeuronPreviewListDto;
import org.ssafy.ssarain.domain.neuron.service.NeuronService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/neurons")
public class NeuronController {

    private final NeuronService      neuronService;
    private final BrainAuthService brainAuthService;
    private final NeuronAuthService  neuronAuthService;

    @GetMapping("/preview/{btid}")
    @Operation(summary = "N01: Neuron 프리 조회")
    public ResponseEntity<BaseResponse<NeuronPreviewListDto>> getNeuronPreview(
            @PathVariable Integer btid
    ) {

        NeuronPreviewListDto neurons = neuronService.getNeuronPreview(btid);

        return BaseResponse.success(SuccessCode.NEURON_INFO_SUCCESS, neurons);
    }

    @GetMapping("/{nid}")
    @Operation(summary = "N02: Neuron 상세 정보 조회")
    public ResponseEntity<BaseResponse<NeuronDetailDto>> getNeuron(
            @PathVariable Integer nid
    ) {

        NeuronDetailDto neuronDetailDto = neuronService.getNeuron(nid);

        return BaseResponse.success(SuccessCode.NEURON_INFO_SUCCESS, neuronDetailDto);
    }

    @PostMapping
    @Operation(summary = "N03: Neuron 추가")
    public ResponseEntity<BaseResponse<NeuronDetailDto>> createNeuron(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid NeuronCreateDto neuronCreateDto
    ) {

        brainAuthService.authorizeBrainMemberByBtid(userDetails, neuronCreateDto.btid());
        NeuronDetailDto neuronDetailDto = neuronService.createNeuron(neuronCreateDto, userDetails.getUserId());

        return BaseResponse.success(SuccessCode.NEURON_CREATE_SUCCESS, neuronDetailDto);
    }

    @DeleteMapping("/{nid}")
    @Operation(summary = "N05: Neuron 삭제")
    public ResponseEntity<BaseResponse<Void>> deleteNeuron(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable int nid
    ) {

        neuronAuthService.authorizeNeuronWriterOrAdmin(userDetails, nid);

        neuronService.deleteNeuron(nid);

        return BaseResponse.success(SuccessCode.NEURON_DELETE_SUCCESS);
    }

}
