package team.seventhmile.tripforp.domain.spot.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.place.dto.CreatePlaceRequest;

@NoArgsConstructor
@Getter
public class CreateSpotRequest {

    @NotNull(message = "장소 정보는 필수입니다.")
    @Valid
    private CreatePlaceRequest place;

    @Min(value = 1, message = "순서는 1 이상이어야 합니다.")
    private int sequence;

    @NotNull(message = "여행 날짜는 필수입니다.")
    @FutureOrPresent(message = "여행 날짜는 현재 또는 미래의 날짜여야 합니다.")
    private LocalDate tripDate;

    @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다.")
    private String memo;

    @Builder
    public CreateSpotRequest(CreatePlaceRequest place, int sequence, LocalDate tripDate, String memo) {
        this.place = place;
        this.sequence = sequence;
        this.tripDate = tripDate;
        this.memo = memo;
    }
}
