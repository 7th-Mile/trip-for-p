package team.seventhmile.tripforp.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.place.entity.Place;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PlaceDto {

    private Long id;
    private String mapPlaceId;

    public PlaceDto(Place place) {
        this.id = place.getId();
        this.mapPlaceId = place.getMapPlaceId();
    }
}
