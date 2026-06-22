package org.ssafy.ssarain.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.common.security.dto.req.SignupReq;
import org.ssafy.ssarain.domain.comment.dao.CommentRepository;
import org.ssafy.ssarain.domain.neuron.dao.NeuronLikeRepository;
import org.ssafy.ssarain.domain.neuron.dao.NeuronRepository;
import org.ssafy.ssarain.domain.user.dao.UserRepository;
import org.ssafy.ssarain.domain.user.dto.UserActivityDto;
import org.ssafy.ssarain.domain.user.dto.UserInfoDto;
import org.ssafy.ssarain.domain.user.dto.UserProfileDto;
import org.ssafy.ssarain.domain.user.dto.UserPasswordUpdateDto;
import org.ssafy.ssarain.domain.user.dto.req.NameCheckReq;
import org.ssafy.ssarain.domain.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository       userRepository;
    private final NeuronRepository     neuronRepository;
    private final CommentRepository    commentRepository;
    private final NeuronLikeRepository neuronLikeRepository;
    private final PasswordEncoder      passwordEncoder;

    @Transactional
    public User createUser(SignupReq dto) {

        String email    = dto.email();
        String name = dto.name();
        String password = dto.password();

        validateUser(email, name);

        String encodedPassword = passwordEncoder.encode(password);

        return userRepository.save(User.of(email, name, encodedPassword));
    }
    
    @Transactional
    public UserProfileDto updateUserPassword(String email, UserPasswordUpdateDto dto) {

        User user = findUserByEmail(email);
        
        if(!isPasswordValid(dto.oldPassword(), user.getPassword())) {
            throw new GlobalException(ErrorCode.USER_PASSWORD_INCORRECT);
        }
        
        if (isPasswordValid(dto.newPassword(), user.getPassword())) {
        	throw new GlobalException(ErrorCode.USER_PASSWORD_CONFLICT);
        }
        
        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        user.updatePassword(encodedPassword);
        return UserProfileDto.from(user);
    }

    @Transactional(readOnly = true)
    public User login(String email, String password) {

        User user = findUserByEmail(email);

        // 비밀번호 불일치
        if(!isPasswordValid(password, user.getPassword())) {
            throw new GlobalException(ErrorCode.USER_AUTH_INFO_INCORRECT);
        }

        return user;
    }

    public UserInfoDto getUserInfo(String email) {

        User user = findUserByEmail(email);
        int neuronCount = neuronRepository.countByUid(user.getUid());
        int commentCount = commentRepository.countByUidAndDeletedAtIsNull(user.getUid());
        int likeCount = neuronLikeRepository.countByNlid_Uid(user.getUid());

        return new UserInfoDto(
                UserProfileDto.from(user),
                new UserActivityDto(neuronCount, commentCount, likeCount));
    }

    public User getUserByUserId(UUID userId) {

        return userRepository.findByUid(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean isNameDuplicate(NameCheckReq nameCheckReq) {

        String name = nameCheckReq.name();
        return userRepository.existsByName(name);
    }

    /*
        Util Method
     */

    // 유저 엔티티 유효성 검사
    private void validateUser(String email, String name) {

        validateDuplicateEmail(email);
        validateDuplicateName(name);
    }

    // 이메일 중복
    private void validateDuplicateEmail(String email) {

        if(userRepository.existsByEmail(email)) {
            throw new GlobalException(ErrorCode.USER_EMAIL_DUPLICATED);
        }
    }

    // 닉네임 중복
    private void validateDuplicateName(String name) {

        if(userRepository.existsByName(name)) {
            throw new GlobalException(ErrorCode.USER_NAME_DUPLICATED);
        }
    }

    private boolean isPasswordValid(String password, String encodedPassword) {

        return passwordEncoder.matches(password, encodedPassword);
    }

    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }
}
