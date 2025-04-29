package team.seventhmile.tripforp.domain.reviewPost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;

public interface ReviewPostRepository extends JpaRepository<ReviewPost, Long>,
	ReviewPostRepositoryCustom {
}
