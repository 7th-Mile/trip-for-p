package team.seventhmile.tripforp.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Boolean existsByNickname(String nickname);
}
