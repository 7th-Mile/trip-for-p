package team.seventhmile.tripforp.domain.user.service;

public interface EmailService {


    void sendVerificationEmail(String email);
    String generateEmailCode();
    void verifyEmailCode(String email, String code);
    void sendPasswordResetEmail(String email);
    void emailGenerateAndSend(String email);

}
