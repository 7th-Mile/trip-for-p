package team.seventhmile.tripforp.domain.region.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.region.entity.Province;
import team.seventhmile.tripforp.domain.region.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long>, RegionRepositoryCustom {

    Optional<Region> findByProvinceAndCity(Province province, String city);

    List<Region> findByProvince(Province province);
}
