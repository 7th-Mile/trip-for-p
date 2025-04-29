package team.seventhmile.tripforp.external.google.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceApiRequest;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceResponse;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesApiRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesRequest;
import team.seventhmile.tripforp.external.google.dto.SearchPlacesResponse;
import team.seventhmile.tripforp.external.google.service.GoogleMapsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-maps")
public class GoogleMapsController {

    private final GoogleMapsService googleMapsService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<SearchPlacesResponse> searchPlaces(
        @ModelAttribute SearchPlacesRequest request
    ) {
        SearchPlacesApiRequest apiRequest = new SearchPlacesApiRequest(request);
        return ResponseEntity.ok(googleMapsService.searchPlacesApi(apiRequest));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') or isAnonymous()")
    @GetMapping("/detail")
    public ResponseEntity<DetailPlaceResponse> detailPlace(
        @ModelAttribute DetailPlaceApiRequest request
    ) {
        return ResponseEntity.ok(googleMapsService.detailPlaceApi(request));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN') or isAnonymous()")
    @GetMapping("/load")
    public ResponseEntity<String> loadMap() {
        return ResponseEntity.ok("Map Loaded");
    }
}
