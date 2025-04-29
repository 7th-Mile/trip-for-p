package team.seventhmile.tripforp.domain.spot.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.place.dto.GetPlaceCountResponse;
import team.seventhmile.tripforp.domain.place.entity.Place;
import team.seventhmile.tripforp.domain.place.service.PlaceService;
import team.seventhmile.tripforp.domain.spot.dto.CreateSpotRequest;
import team.seventhmile.tripforp.domain.spot.dto.GetPopularPlaceResponse;
import team.seventhmile.tripforp.domain.spot.dto.UpdateSpotRequest;
import team.seventhmile.tripforp.domain.spot.entity.Spot;
import team.seventhmile.tripforp.domain.spot.repository.SpotRepository;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceApiRequest;
import team.seventhmile.tripforp.external.google.dto.DetailPlaceResponse;
import team.seventhmile.tripforp.external.google.service.GoogleMapsService;
import team.seventhmile.tripforp.global.common.RequestSizeHolder;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SpotService {

    private final PlaceService placeService;
    private final SpotRepository spotRepository;
    private final GoogleMapsService googleMapsService;

    @Transactional
    public void createSpot(Course course, CreateSpotRequest request) {
        Place place = placeService.findOrCreatePlace(request.getPlace());
        Spot spot = Spot.builder()
            .place(place)
            .sequence(request.getSequence())
            .tripDate(request.getTripDate())
            .memo(request.getMemo())
            .build();
        course.addSpot(spot);
    }

    @Transactional
    public void manageSpots(Course course, List<UpdateSpotRequest> requests) {

        requests.stream()
            .filter(req -> req.getAction().equals("delete"))
            .forEach(req -> {
                Spot spot = spotRepository.findById(req.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Spot.class, req.getId()));
                course.removeSpot(spot);
            });
        spotRepository.flush();

        int tempSequence = 100;
        for (UpdateSpotRequest request : requests) {
            if (request.getAction().equals("update")) {
                Spot spot = spotRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Spot.class, request.getId()));
                spot.initSequence(tempSequence++);
            }
        }
        spotRepository.flush();

        requests.stream()
            .filter(req -> req.getAction().equals("update"))
            .forEach(req -> {
                Place place = placeService.findOrCreatePlace(req.getPlace());
                Spot spot = spotRepository.findById(req.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Spot.class, req.getId()));
                spot.updateSpot(place, req);
            });
        spotRepository.flush();

        requests.stream()
            .filter(req -> req.getAction().equals("create"))
            .forEach(req -> {
                Place place = placeService.findOrCreatePlace(req.getPlace());
                Spot saveSpot = Spot.builder()
                    .place(place)
                    .sequence(req.getSequence())
                    .tripDate(req.getTripDate())
                    .memo(req.getMemo())
                    .build();
                course.addSpot(saveSpot);
            });
    }

    public List<GetPopularPlaceResponse> getPopularPlaces(String city, int size) {
        List<GetPlaceCountResponse> places = spotRepository.getPlaceCount(city, size);
        RequestSizeHolder.setSize(places.size());
        return places.stream()
            .map(p -> {
                DetailPlaceResponse response = googleMapsService.detailPlaceApi(
                    DetailPlaceApiRequest.builder().id(p.getPlace().getMapPlaceId()).build());
                return GetPopularPlaceResponse.builder()
                    .place(response)
                    .count(p.getCount())
                    .build();
            })
            .toList();

    }

}
