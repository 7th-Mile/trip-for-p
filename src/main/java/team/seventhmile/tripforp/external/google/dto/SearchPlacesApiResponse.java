package team.seventhmile.tripforp.external.google.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchPlacesApiResponse {
    private List<GoogleMapsPlaceApiDto> places = new ArrayList<>();
    private String nextPageToken;
}
