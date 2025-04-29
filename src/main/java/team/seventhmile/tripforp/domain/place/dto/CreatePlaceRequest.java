package team.seventhmile.tripforp.domain.place.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreatePlaceRequest {

    private String mapPlaceId;

    @Builder
    public CreatePlaceRequest(String mapPlaceId) {
        this.mapPlaceId = mapPlaceId;
    }
}
