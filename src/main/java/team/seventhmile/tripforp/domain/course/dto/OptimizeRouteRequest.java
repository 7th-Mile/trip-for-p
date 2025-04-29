package team.seventhmile.tripforp.domain.course.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptimizeRouteRequest {
    private final List<Place> places = new ArrayList<>();

    @Getter
    @NoArgsConstructor
    public static class Place {
        private int sequence;
        private Location location;

        @Getter
        @NoArgsConstructor
        public static class Location {
            private double lat;
            private double lng;
        }
    }
}


