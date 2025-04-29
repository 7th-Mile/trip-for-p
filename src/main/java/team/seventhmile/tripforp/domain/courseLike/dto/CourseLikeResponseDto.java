package team.seventhmile.tripforp.domain.courseLike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseLikeResponseDto {
    private boolean liked;
    private String message;
}
