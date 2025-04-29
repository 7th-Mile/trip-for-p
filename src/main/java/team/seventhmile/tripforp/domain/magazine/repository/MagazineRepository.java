package team.seventhmile.tripforp.domain.magazine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;

public interface MagazineRepository extends JpaRepository<Magazine, Long>,
    MagazineRepositoryCustom {

    Page<Magazine> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

