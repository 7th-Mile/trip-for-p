package team.seventhmile.tripforp.domain.spot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.spot.dto.UpdateSpotRequest;
import team.seventhmile.tripforp.domain.place.entity.Place;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "spots",
    uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "sequence", "trip_date"}))
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private LocalDate tripDate;

    @Column
    private String memo;

    @Builder
    public Spot(Place place, int sequence, LocalDate tripDate, String memo) {
        this.place = place;
        this.sequence = sequence;
        this.tripDate = tripDate;
        this.memo = memo;
    }

    public Long updateSpot(Place place, UpdateSpotRequest request) {
        this.place = place;
        this.sequence = request.getSequence();
        this.tripDate = request.getTripDate();
        this.memo = request.getMemo();

        return id;
    }

    public void initSequence(int sequence) {
        this.sequence = sequence;
    }
}
