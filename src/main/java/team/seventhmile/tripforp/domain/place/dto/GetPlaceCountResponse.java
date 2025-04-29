package team.seventhmile.tripforp.domain.place.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GetPlaceCountResponse {

    private PlaceDto place;
    private Long count;

    @QueryProjection
    public GetPlaceCountResponse(Long id, String mapPlaceId, Long count) {
        this.place = new PlaceDto(id, mapPlaceId);
        this.count = count;
    }
}
