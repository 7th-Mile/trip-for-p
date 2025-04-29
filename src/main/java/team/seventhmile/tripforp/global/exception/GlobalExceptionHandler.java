package team.seventhmile.tripforp.global.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //유효성 체크
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            String defaultMessage = error.getDefaultMessage();

            String simplifiedField = field.contains(".") ?
                field.substring(field.lastIndexOf('.') + 1) : field;

            errors.add(String.format("%s: %s", simplifiedField, defaultMessage));
        }

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("입력값 검증에 실패했습니다.")
            .errors(errors)
            .path(request.getDescription(false).substring(4))
            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //리소스 존재 체크
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
        WebRequest request) {
        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .path(request.getDescription(false).substring(4))
            .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //게시글 소유자 체크
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex,
        WebRequest request) {
        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .message(ex.getMessage())
            .path(request.getDescription(false).substring(4))
            .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    //데이터베이스 무결성 제약 조건 위반
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(
        SQLIntegrityConstraintViolationException ex, WebRequest request) {
        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .path(request.getDescription(false).substring(4))
            .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    //회원 관련
    @ExceptionHandler(AuthCustomException.class)
    public ResponseEntity<ErrorResponse> handleAuthCustomException(AuthCustomException ex, WebRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .message(ex.getErrorMessage() != null ? ex.getErrorMessage() : errorCode.getMessage())
                .path(request.getDescription(false))
                .build();
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
