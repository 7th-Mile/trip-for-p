package team.seventhmile.tripforp.domain.region.repository;

import java.util.List;
import team.seventhmile.tripforp.domain.region.dto.GetPopularRegionsResponse;

public interface RegionRepositoryCustom {

    List<GetPopularRegionsResponse> getPopularRegions(int size);
}
