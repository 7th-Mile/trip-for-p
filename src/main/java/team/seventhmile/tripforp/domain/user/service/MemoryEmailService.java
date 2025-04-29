package team.seventhmile.tripforp.domain.user.service;

import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemoryEmailService implements EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final CacheManager cacheManager;

    @Override
    public void sendVerificationEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getIsDeleted()) {
                throw new AuthCustomException(ErrorCode.WITHDRAWN_USER);
            }
            throw new AuthCustomException(ErrorCode.EMAIL_ALREADY_IN_USE);
        }
        emailGenerateAndSend(email);
    }

    @Override
    public String generateEmailCode() {
        int codeLength = 6;
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(codeLength);
        Random random = new Random();

        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(chars.length());
            char randomChar = chars.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString().trim();
    }

    @Override
    public void verifyEmailCode(String email, String code) {
        Cache cache = cacheManager.getCache("emailCodes");
        ValueWrapper wrapper = cache.get(email);
        if (wrapper == null) {
            throw new AuthCustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND);
        }
        String saved = (String) wrapper.get();
        if (!saved.equals(code)) {
            throw new AuthCustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        cache.put(email, "verified");
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getIsDeleted()) {
                throw new AuthCustomException(ErrorCode.WITHDRAWN_USER);
            }
        } else {
            throw new AuthCustomException(ErrorCode.USER_NOT_FOUND_IN_DATABASE);
        }

        emailGenerateAndSend(email);
    }

    @Override
    public void emailGenerateAndSend(String email) {
        String emailCode = generateEmailCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(String.format("%s <%s>", "Trip For P", fromEmail));
        message.setTo(email);
        message.setSubject("[Trip For P] 인증코드 발송 안내"); //제목 설정
        message.setText("귀하의 인증 코드는 " + emailCode + "입니다.\n인증 코드는 5분 간 유지됩니다.");
        try {
            javaMailSender.send(message);
            cacheManager.getCache("emailCodes").put(email, emailCode);
            log.info("캐시: {}", cacheManager.getCache("emailCodes").get(email).get());
        } catch (Exception e) {
            log.error("이메일 전송 중 오류 발생", e);
            throw new AuthCustomException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }
}
