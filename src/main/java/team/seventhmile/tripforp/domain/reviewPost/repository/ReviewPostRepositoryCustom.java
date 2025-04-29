package team.seventhmile.tripforp.domain.reviewPost.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;

public interface ReviewPostRepositoryCustom {

	Page<ReviewPost> getReviewPosts(Pageable pageable);

	Page<ReviewPost> getReviewPostKeywordContaining(String keyword, Pageable pageable);


	Page<ReviewPost> getMyReviews(String email, Pageable pageable);
}
