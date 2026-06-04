package org.ssafy.ssarain.infra.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.ssafy.ssarain.common.error.GlobalException;
import org.ssafy.ssarain.common.response.ErrorCode;
import org.ssafy.ssarain.domain.user.dao.UserRepository;
import org.ssafy.ssarain.infra.redis.dao.RedisRepository;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final RedisRepository redisRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String MAIL_PREFIX = "mail_auth:";
    private static final String MAIL_VERIFY_TOKEN_PREFIX = "mail_auth_verify:";
    private static final Duration VERIFICATION_LIMIT_TIME = Duration.ofMinutes(5);
    private static final Duration VERIFICATION_TOKEN_LIMIT_TIME = Duration.ofMinutes(10);

    public void sendVerificationCode(String email) {

        // 이메일 중복 체크
        if(userRepository.existsByEmail(email)) {
            throw new GlobalException(ErrorCode.USER_EMAIL_DUPLICATED);
        }

        String title = "회원가입 이메일 인증 번호";
        String code = createCode();

        redisRepository.setValue(MAIL_PREFIX + email, code, VERIFICATION_LIMIT_TIME);

        sendEmail(email, title, "인증번호는 [" + code + "] 입니다. 3분 내에 입력해주세요.");
    }

    public boolean verifyCode(String email, String code) {

        String key = MAIL_PREFIX + email;
        String savedCode = redisRepository.getValue(key, String.class)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMAIL_VERIFY_CODE_NOT_FOUND));

        if(savedCode.equals(code)) {
            redisRepository.deleteData(key);

            String mailVerifyKey = MAIL_VERIFY_TOKEN_PREFIX + email;
            redisRepository.setValue(mailVerifyKey, "true", VERIFICATION_TOKEN_LIMIT_TIME);
            return true;
        }

        return false;
    }

    public boolean isEmailVerified(String email) {
        String mailVerifyKey = MAIL_VERIFY_TOKEN_PREFIX + email; // 내부 상수 사용

        return redisRepository.getValue(mailVerifyKey, String.class)
                .map(status -> status.equals("true"))
                .orElse(false);
    }

    @Async
    protected void sendEmail(String email, String title, String content) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
    }

    private String createCode() {

        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
