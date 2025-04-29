package team.seventhmile.tripforp.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;
import team.seventhmile.tripforp.global.common.BaseEntity;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "places")
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;
    private String mapPlaceId;

    @Builder
    public Place(String mapPlaceId) {
        this.mapPlaceId = mapPlaceId;
    }
}


