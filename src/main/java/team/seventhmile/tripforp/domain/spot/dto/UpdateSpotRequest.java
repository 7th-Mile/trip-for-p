package team.seventhmile.tripforp.domain.spot.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.place.dto.CreatePlaceRequest;

@Getter
@NoArgsConstructor
public class UpdateSpotRequest {

    private Long id;
    private String action;
    private int sequence;
    private LocalDate tripDate;
    private String memo;
    private CreatePlaceRequest place;
}
