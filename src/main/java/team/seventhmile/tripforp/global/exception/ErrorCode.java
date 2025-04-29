package team.seventhmile.tripforp.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "400001", "잘못된 인증 코드입니다."),
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "400002", "필수 입력 항목이 누락되었습니다."),
    EMAIL_NOT_FOUND_IN_TOKEN(HttpStatus.BAD_REQUEST, "400003", "이메일을 찾을 수 없습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "400004", "인증 코드가 만료되었습니다."),
    NICKNAME_TOO_SHORT(HttpStatus.BAD_REQUEST,"400005", "닉네임이 너무 짧습니다. 2자 이상 입력해주세요."),
    NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST,"400006", "닉네임이 너무 깁니다. 10자 이하로 입력해주세요."),
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "401001", "액세스 토큰을 찾을 수 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401002", "액세스 토큰이 만료되었습니다."),
    WITHDRAWN_USER(HttpStatus.FORBIDDEN, "403002", "탈퇴한 사용자입니다."),
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "404001", "인증 코드를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "40402", "해당 사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_IN_USE(HttpStatus.CONFLICT, "409001", "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_IN_USE(HttpStatus.CONFLICT, "409002", "이미 사용 중인 닉네임입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "403001", "이메일 인증이 완료되지 않았습니다."),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500001", "이메일 전송 중 오류가 발생했습니다."),
    USER_NOT_FOUND_IN_DATABASE(HttpStatus.INTERNAL_SERVER_ERROR, "500002", "사용자를 찾을 수 없습니다."),
    PASSWORD_CHANGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500003", "비밀번호 변경 중 오류가 발생했습니다."),
    REDIS_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "503001", "일시적인 Redis 연결 오류");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
