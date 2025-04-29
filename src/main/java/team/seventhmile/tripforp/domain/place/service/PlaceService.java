package team.seventhmile.tripforp.domain.place.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.place.dto.CreatePlaceRequest;
import team.seventhmile.tripforp.domain.place.entity.Place;
import team.seventhmile.tripforp.domain.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public Place findOrCreatePlace(CreatePlaceRequest request) {
        return placeRepository.findByMapPlaceId(request.getMapPlaceId())
            .orElseGet(() -> {
                Place newPlace = Place.builder()
                    .mapPlaceId(request.getMapPlaceId())
                    .build();
                return placeRepository.save(newPlace);
            });
    }

}
