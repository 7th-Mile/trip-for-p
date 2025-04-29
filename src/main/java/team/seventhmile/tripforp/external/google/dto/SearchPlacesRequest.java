package team.seventhmile.tripforp.external.google.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchPlacesRequest {
    private String textQuery;
    private int pageSize;
    private String pageToken;
    private double latitude;
    private double longitude;
}
