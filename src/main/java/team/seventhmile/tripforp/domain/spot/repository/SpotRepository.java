package team.seventhmile.tripforp.domain.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.spot.entity.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long>, SpotRepositoryCustom {

}
