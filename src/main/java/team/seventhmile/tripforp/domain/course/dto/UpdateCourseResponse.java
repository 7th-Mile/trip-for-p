package team.seventhmile.tripforp.domain.course.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateCourseResponse {

    private Long id;

    @Builder
    public UpdateCourseResponse(Long id) {
        this.id = id;
    }
}
