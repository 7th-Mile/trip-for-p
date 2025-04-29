package team.seventhmile.tripforp.domain.course.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.spot.dto.CreateSpotRequest;

@NoArgsConstructor
@Getter
public class CreateCourseRequest {

    @NotNull(message = "출발 날짜는 필수입니다.")
    @FutureOrPresent(message = "출발 날짜는 현재 또는 미래의 날짜여야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "도착 날짜는 필수입니다.")
    @FutureOrPresent(message = "도착 날짜는 현재 또는 미래의 날짜여야 합니다.")
    private LocalDate endDate;

    @NotBlank(message = "상위지역은 필수입니다.")
    private String province;

    @NotBlank(message = "하위지역은 필수입니다.")
    private String city;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 1, max = 100, message = "제목은 1자 이상 100자 이하여야 합니다.")
    private String title;

    @NotEmpty(message = "최소 하나의 여행 코스 항목이 필요합니다.")
    @Size(max = 30, message = "최대 30개의 계획 항목만 허용됩니다.")
    @Valid
    private List<CreateSpotRequest> spots;

    @AssertTrue(message = "도착 날짜는 출발 날짜보다 뒤여야 합니다.")
    private boolean isEndDateAfterStartDate() {
        return startDate != null && endDate != null && (endDate.isEqual(startDate) || endDate.isAfter(startDate));
    }
}
