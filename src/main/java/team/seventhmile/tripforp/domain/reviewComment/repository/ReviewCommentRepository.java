package team.seventhmile.tripforp.domain.reviewComment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.reviewComment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

	Page<ReviewComment> findByReviewPost(ReviewPost reviewPost, Pageable pageable);

	Optional<ReviewComment> findByIdAndAuthor(Long id, User author);

    Page<ReviewComment> findByAuthor_Email(String email, Pageable pageable);
}
