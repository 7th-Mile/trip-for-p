package team.seventhmile.tripforp.domain.course.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.course.entity.Course;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetPopularCourseResponse {

    private Long id;
    private String title;
    private long likeCount;
    private String imageUrl;

    @QueryProjection
    public GetPopularCourseResponse(Course course, long likeCount, String imageUrl) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.likeCount = likeCount;
        this.imageUrl = imageUrl;
    }
}
