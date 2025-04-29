package team.seventhmile.tripforp.domain.user.service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class RedisEmailService implements EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.mail.username}")
    private String fromEmail;

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
        Object storedCodeObj = redisTemplate.opsForValue().get("EMAIL_CODE:" + email);
        if (storedCodeObj == null) {
            throw new AuthCustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND);
        }

        String storedCode = storedCodeObj.toString();
        if (!storedCode.equals(code)) {
            throw new AuthCustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        redisTemplate.opsForValue().set("EMAIL_VERIFIED:" + email, "true", 5, TimeUnit.MINUTES);
        redisTemplate.delete("EMAIL_CODE:" + email);
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
        message.setSubject("[Trip For P] 회원가입 인증코드 발송 안내");
        message.setText("귀하의 인증 코드는 " + emailCode + "입니다.\n인증 코드는 5분 간 유지됩니다.");
        try {
            javaMailSender.send(message);
            redisTemplate.opsForValue().set("EMAIL_CODE:" + email, emailCode, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new AuthCustomException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }
}
