package team.seventhmile.tripforp.domain.spot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceResponse;

@Getter
@NoArgsConstructor
public class GetPopularPlaceResponse {
    private DetailPlaceResponse place;
    private Long count;

    @Builder
    public GetPopularPlaceResponse(DetailPlaceResponse place, Long count) {
        this.place = place;
        this.count = count;
    }
}
