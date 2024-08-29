package team.seventhmile.tripforp.global.exception;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ErrorResponse {

    private int status;
    private String message;
    private Map<String, String> errors;
    private String path;

    @Builder
    public ErrorResponse(int status, String message, Map<String, String> errors, String path) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.path = path;
    }
}
