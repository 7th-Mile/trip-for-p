package team.seventhmile.tripforp.domain.course.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateCourseResponse {

    private Long id;

    @Builder
    public CreateCourseResponse(Long id) {
        this.id = id;
    }
}
