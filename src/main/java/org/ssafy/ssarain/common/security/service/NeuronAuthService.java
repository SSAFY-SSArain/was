package org.ssafy.ssarain.common.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.neuron.dao.NeuronRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NeuronAuthService {

    private final NeuronRepository neuronRepository;
    private final AuthService    authService;

    public void authorizeNeuronWriter(CustomUserDetails userDetails, int nid) {

        if(!isNeuronWriter(userDetails.getUserId(), nid)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void authorizeNeuronWriterOrAdmin(CustomUserDetails userDetails, int nid) {

        if(authService.isAdmin(userDetails)){
            return;
        }

        authorizeNeuronWriter(userDetails, nid);
    }

    /*
        Util Method
     */

    private boolean isNeuronWriter(UUID uid, int nid) {
        return neuronRepository.existsByNidAndUser_Uid(nid, uid);
    }

}
