package team.seventhmile.tripforp.external.google.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailPlaceApiRequest {
    private String id;
    private final String languageCode = "ko";
    private final String regionCode = "kr";

    @Builder
    public DetailPlaceApiRequest(String id) {
        this.id = id;
    }
}
