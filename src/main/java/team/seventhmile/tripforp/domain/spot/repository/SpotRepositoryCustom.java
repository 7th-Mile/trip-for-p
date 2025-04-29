package team.seventhmile.tripforp.domain.spot.repository;

import java.util.List;
import team.seventhmile.tripforp.domain.place.dto.GetPlaceCountResponse;

public interface SpotRepositoryCustom {
    List<GetPlaceCountResponse> getPlaceCount(String city, int size);

}
