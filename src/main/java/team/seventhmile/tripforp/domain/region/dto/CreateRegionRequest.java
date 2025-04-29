package team.seventhmile.tripforp.domain.region.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRegionRequest {
    private String province;
    private String city;

}
