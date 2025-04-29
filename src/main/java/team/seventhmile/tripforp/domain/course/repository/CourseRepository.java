package team.seventhmile.tripforp.domain.course.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {
    @EntityGraph(attributePaths = {"spots.place", "spots", "courseLikes"})
    Optional<Course> findById(Long id);

    Optional<Course> findByIdAndCreator(Long id, User user);
}
