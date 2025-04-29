package team.seventhmile.tripforp.domain.course.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.spot.dto.UpdateSpotRequest;

@NoArgsConstructor
@Getter
public class UpdateCourseRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String province;
    private String city;
    private List<UpdateSpotRequest> spots;
}
