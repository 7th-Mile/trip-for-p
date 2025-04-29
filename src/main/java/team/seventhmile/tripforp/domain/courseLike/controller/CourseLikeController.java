package team.seventhmile.tripforp.domain.courseLike.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.courseLike.dto.CourseLikeResponseDto;
import team.seventhmile.tripforp.domain.courseLike.service.CourseLikeService;
import team.seventhmile.tripforp.global.common.PageResponse;

@RestController
@RequestMapping("/api/course-likes")
public class CourseLikeController {

    private final CourseLikeService courseLikeService;

    public CourseLikeController(CourseLikeService courseLikeService) {
        this.courseLikeService = courseLikeService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/courses/{id}")
    public ResponseEntity<CourseLikeResponseDto> toggleLikeCourse(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable(name = "id") Long id) {

        CourseLikeResponseDto responseDto = courseLikeService.toggleLikeCourse(
            userDetails.getUsername(),
            id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<PageResponse<GetCourseListResponse>> getMyFavCourseList(
        @AuthenticationPrincipal UserDetails user,
        Pageable pageable) {
        return ResponseEntity.ok(courseLikeService.getMyFavCourseList(user, pageable));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkCourseLike(
        @AuthenticationPrincipal UserDetails user,
        @RequestParam(name = "courseId") Long courseId
    ) {
        return ResponseEntity.ok(courseLikeService.checkCourseLike(user.getUsername(), courseId));
    }
}
