package team.seventhmile.tripforp.domain.freePost.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.freePost.entity.FreePost;


@Repository
public interface FreePostRepository extends JpaRepository<FreePost, Long>, FreePostRepositoryCustom {

    Page<FreePost> findByUserEmailOrderByCreatedAtDesc(String email, Pageable pageable);
}
