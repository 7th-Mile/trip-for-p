package team.seventhmile.tripforp.external.google.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchPlacesApiRequest {
    private String textQuery;
    private final String languageCode = "ko";
    private final String regionCode = "kr";
    private int pageSize;
    private String pageToken;
    private LocationBias locationBias;

    public SearchPlacesApiRequest(SearchPlacesRequest request) {
        this.textQuery = request.getTextQuery();
        this.pageSize = request.getPageSize();
        this.locationBias = new LocationBias(
            new Circle(
                new Center(request.getLatitude(), request.getLongitude()),
                50000.0
            )
        );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class LocationBias {
        private Circle circle;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Circle {
        private Center center;
        private double radius;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Center {
        private double latitude;
        private double longitude;
    }


}
