package team.seventhmile.tripforp.domain.course.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.course.dto.UpdateCourseRequest;
import team.seventhmile.tripforp.domain.courseLike.entity.CourseLike;
import team.seventhmile.tripforp.domain.region.entity.Region;
import team.seventhmile.tripforp.domain.spot.entity.Spot;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "courses")
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spot> spots = new ArrayList<>();  // Spots associated with this Course

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseLike> courseLikes = new ArrayList<>();  // Spots associated with this Course

    @Column(nullable = false)
    private int views;

    /**
     * 생성 메서드
     */
    @Builder
    public Course(User user, Region region, LocalDate startDate, LocalDate endDate, String title) {
        this.creator = user;
        this.region = region;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.views = 0;
    }

    /**
     * 수정 메서드
     */
    public void updateCourse(UpdateCourseRequest request) {
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.title = request.getTitle();
    }

    public void addSpot(Spot spot) {
        this.spots.add(spot);
        spot.setCourse(this);
    }

    public void removeSpot(Spot spot) {
        this.spots.remove(spot);
        spot.setCourse(null);
    }

    public void increaseViews() {
        this.views += 1;
    }
}
