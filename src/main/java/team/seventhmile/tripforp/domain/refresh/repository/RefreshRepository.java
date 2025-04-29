package team.seventhmile.tripforp.domain.refresh.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.refresh.entity.Refresh;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    Boolean existsByUsername(String username);

    Optional<Refresh> findByUsername(String username);

    void deleteByUsername(String username);

}
