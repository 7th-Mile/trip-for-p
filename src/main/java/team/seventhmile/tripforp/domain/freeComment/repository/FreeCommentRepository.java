package team.seventhmile.tripforp.domain.freeComment.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.freeComment.entity.FreeComment;
import team.seventhmile.tripforp.domain.freePost.entity.FreePost;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface FreeCommentRepository extends JpaRepository<FreeComment, Long> {
	Page<FreeComment> findByFreePost(FreePost freePost, Pageable pageable);

	Optional<FreeComment> findByIdAndAuthor(Long id, User author);

    Page<FreeComment> findByAuthor_Email(String username, Pageable pageable);
}
