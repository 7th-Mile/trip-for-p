package team.seventhmile.tripforp.external.google.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class PhotoPlaceResponse {

    private String id;
    private String name;
    private String address;
    @Setter
    private String photoUri;
    private String placeUri;

    public PhotoPlaceResponse(GoogleMapsPlaceApiDto place) {
        this.id = place.getId();
        this.name = place.getDisplayName().getText();
        this.address = place.getFormattedAddress();
        this.photoUri = place.getPhotos().get(0).getName();
        this.placeUri = place.getGoogleMapsLinks().getPlaceUri();
    }
}
