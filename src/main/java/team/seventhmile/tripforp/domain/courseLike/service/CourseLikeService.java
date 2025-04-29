package team.seventhmile.tripforp.domain.courseLike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.course.repository.CourseRepository;
import team.seventhmile.tripforp.domain.courseLike.dto.CourseLikeResponseDto;
import team.seventhmile.tripforp.domain.courseLike.entity.CourseLike;
import team.seventhmile.tripforp.domain.courseLike.repository.CourseLikeRepository;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.common.PageResponse;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CourseLikeService {

    private final CourseLikeRepository courseLikeRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public CourseLikeResponseDto toggleLikeCourse(String email, Long courseId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        CourseLike existingLike = courseLikeRepository.findByUserIdAndCourseId(user.getId(),
            courseId);

        if (existingLike != null) {
            courseLikeRepository.delete(existingLike);
            return new CourseLikeResponseDto(false, "좋아요 취소됨");
        } else {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("플랜을 찾을 수 없습니다."));

            CourseLike newLike = new CourseLike(null, user, course);
            courseLikeRepository.save(newLike);
            return new CourseLikeResponseDto(true, "좋아요 완료");
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<GetCourseListResponse> getMyFavCourseList(UserDetails user, Pageable pageable) {
        Page<Course> myFavCourses = courseLikeRepository.findCoursesByUserEmail(user.getUsername(),
            pageable);
        return new PageResponse<>(myFavCourses.map(course -> {
            long likeCount = courseLikeRepository.countByCourseId(course.getId());
            return new GetCourseListResponse(course, likeCount);
        }));
    }

    public Boolean checkCourseLike(String email, Long courseId) {
        User findUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(User.class));
        return courseLikeRepository.existsCourseLikeByUserIdAndCourseId(findUser.getId(), courseId);
    }
}
