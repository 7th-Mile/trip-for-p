package team.seventhmile.tripforp.domain.user.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.user.dto.ApiResponse;
import team.seventhmile.tripforp.domain.user.dto.EmailCodeRequest;
import team.seventhmile.tripforp.domain.user.service.EmailService;

@RestController
@RequestMapping("/api/mails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(@Qualifier("memoryEmailService") EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationEmail(
        @Valid @RequestBody EmailCodeRequest emailCodeReq) {
        emailService.sendVerificationEmail(emailCodeReq.getEmail());
        return ResponseEntity.ok(new ApiResponse("success", "인증 이메일이 전송되었습니다."));
    }

    @PostMapping("/verification")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailCodeRequest emailCodeReq) {
        emailService.verifyEmailCode(emailCodeReq.getEmail(), emailCodeReq.getCode());
        return ResponseEntity.ok(new ApiResponse("success", "이메일이 성공적으로 인증되었습니다."));
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> sendPasswordResetEmail(
        @Valid @RequestBody EmailCodeRequest emailCodeReq) {
        emailService.sendPasswordResetEmail(emailCodeReq.getEmail());
        return ResponseEntity.ok(new ApiResponse("success", "인증 이메일이 전송되었습니다."));
    }
}
