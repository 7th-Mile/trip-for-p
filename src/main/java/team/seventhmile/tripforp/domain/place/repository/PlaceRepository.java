package team.seventhmile.tripforp.domain.place.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByMapPlaceId(String id);
}
