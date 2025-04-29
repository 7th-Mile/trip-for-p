package team.seventhmile.tripforp.external.google.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceApiRequest;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceResponse;
import team.seventhmile.tripforp.external.google.dto.GoogleMapsPlaceApiDto;
import team.seventhmile.tripforp.external.google.dto.SearchPlaceResponse;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiResponse;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesResponse;

@Service
@Transactional(readOnly = true)
public class GoogleMapsService {

    private final RestTemplate restTemplate;

    public GoogleMapsService(@Qualifier("googleMapsRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SearchPlacesResponse searchPlacesApi(SearchPlacesApiRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Goog-FieldMask",
            "places.id,places.displayName.text,places.formattedAddress,places.rating,places.userRatingCount,places.primaryTypeDisplayName,places.location,places.googleMapsUri,nextPageToken");

        HttpEntity<SearchPlacesApiRequest> entity = new HttpEntity<>(
            request, headers);

        SearchPlacesApiResponse response = restTemplate.postForObject(
            "/places:searchText",
            entity,
            SearchPlacesApiResponse.class
        );

        List<SearchPlaceResponse> places = response.getPlaces().stream()
            .map(SearchPlaceResponse::new)
            .toList();

        return SearchPlacesResponse.builder()
            .places(places)
            .nextPageToken(response.getNextPageToken())
            .build();
    }

    public DetailPlaceResponse detailPlaceApi(DetailPlaceApiRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Goog-FieldMask",
            "id,location,displayName,formattedAddress,primaryTypeDisplayName,rating,userRatingCount,googleMapsUri");

        String url = UriComponentsBuilder.fromPath("/places/" + request.getId())
            .queryParam("languageCode", request.getLanguageCode())
            .queryParam("regionCode", request.getRegionCode())
            .build()
            .toString();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        GoogleMapsPlaceApiDto response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            GoogleMapsPlaceApiDto.class
        ).getBody();

        return new DetailPlaceResponse(response);
    }
}
