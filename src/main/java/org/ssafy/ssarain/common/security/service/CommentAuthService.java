package org.ssafy.ssarain.common.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.model.CustomUserDetails;
import org.ssafy.ssarain.domain.comment.dao.CommentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentAuthService {

    private final CommentRepository commentRepository;
    private final AuthService authService;


    public void authorizeCommentWriter(CustomUserDetails userDetails, int cid) {

        if(!isCommentWriter(userDetails.getUserId(), cid)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void authorizeCommentWriterOrAdmin(CustomUserDetails userDetails, int cid) {

        if(isCommentWriter(userDetails.getUserId(), cid)) {
            return;
        }

        if(authService.isAdmin(userDetails)) {
            return;
        }

        throw new GlobalException(ErrorCode.ACCESS_DENIED);
    }

    /*
        Util Method
     */

    private boolean isCommentWriter(UUID uid, int cid) {
        return commentRepository.existsByCidAndUser_uid(cid, uid);
    }

}
