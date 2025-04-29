package team.seventhmile.tripforp.domain.course.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.region.entity.Region;
import team.seventhmile.tripforp.domain.spot.dto.CourseGetItemDto;

@NoArgsConstructor
@Getter
@Setter
public class GetCourseResponse {

    private Long id;
    private String writer;
    private String title;
    private String province;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private int views;
    private long likeCount;
    private List<CourseGetItemDto> spots;
    private ZonedDateTime createdAt;

    @QueryProjection
    public GetCourseResponse(Course course, long likeCount) {
        this.id = course.getId();
        this.writer = course.getCreator().getNickname();
        this.title = course.getTitle();
        this.province = course.getRegion().getProvince().getName();
        this.city = course.getRegion().getCity();
        this.startDate = course.getStartDate();
        this.endDate = course.getEndDate();
        this.views = course.getViews();
        this.likeCount = likeCount;
        this.spots = course.getSpots().stream()
            .map(CourseGetItemDto::new)
            .toList();
        this.createdAt = course.getCreatedAt();
    }

    @QueryProjection
    public GetCourseResponse(Long id, String writer, String title, Region region,
        LocalDate startDate, LocalDate endDate, int views, long likeCount,
        ZonedDateTime createdAt) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.province = region.getProvince().getName();
        this.city = region.getCity();
        this.startDate = startDate;
        this.endDate = endDate;
        this.views = views;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }
}
