package team.seventhmile.tripforp.external.google.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleMapsPlaceApiDto {

    private String id;
    private String formattedAddress;
    private double rating;
    private int userRatingCount;
    private PlaceName displayName;
    private PlaceType primaryTypeDisplayName;
    private PlaceLocation location;
    private final List<Photo> photos = new ArrayList<>();
    private GoogleMapsLinks googleMapsLinks;
    private String googleMapsUri;

    public PlaceType getPrimaryTypeDisplayName() {
        return primaryTypeDisplayName != null ? primaryTypeDisplayName : new PlaceType();
    }

    @Getter
    @NoArgsConstructor
    public static class PlaceName {
        private String text;
    }

    @Getter
    @NoArgsConstructor
    public static class PlaceType {
        private String text;
    }

    @Getter
    @NoArgsConstructor
    public static class PlaceLocation {
        private double latitude;
        private double longitude;
    }

    @Getter
    @NoArgsConstructor
    public static class Photo {
        private String name;
        private int widthPx;
        private int heightPx;
        private final List<AuthorAttribution> authorAttributions = new ArrayList<>();
        private String flagContentUri;
        private String googleMapsUri;


        @Getter
        @NoArgsConstructor
        public static class AuthorAttribution {

            private String displayName;
            private String uri;
            private String photoUri;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class GoogleMapsLinks {

        private String directionsUri;
        private String placeUri;
        private String writeAReviewUri;
        private String reviewsUri;
        private String photosUri;
    }
}
