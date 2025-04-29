package team.seventhmile.tripforp.domain.region.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.region.entity.Province;

@Getter
@NoArgsConstructor
public class GetPopularRegionsResponse {

    private Long id;
    private String province;
    private String city;
    private long count;

    @QueryProjection
    public GetPopularRegionsResponse(Long id, Province province, String city, long count) {
        this.id = id;
        this.province = province.getName();
        this.city = city;
        this.count = count;
    }
}
