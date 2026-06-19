package org.ssafy.ssarain.common.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.node.dao.NodeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NodeAuthService {

    private final NodeRepository nodeRepository;
    private final AuthService    authService;

    public void authorizeNodeWriter(CustomUserDetails userDetails, int nid) {

        if(!isNodeWriter(userDetails.getUserId(), nid)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void authorizeNodeWriterOrAdmin(CustomUserDetails userDetails, int nid) {

        if(authService.isAdmin(userDetails)){
            return;
        }

        authorizeNodeWriter(userDetails, nid);
    }

    /*
        Util Method
     */

    private boolean isNodeWriter(UUID uid, int nid) {
        return nodeRepository.existsByNidAndUser_Uid(nid, uid);
    }

}
