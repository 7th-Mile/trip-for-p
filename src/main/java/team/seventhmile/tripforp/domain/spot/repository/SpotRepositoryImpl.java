package team.seventhmile.tripforp.domain.spot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.course.entity.QCourse;
import team.seventhmile.tripforp.domain.place.dto.GetPlaceCountResponse;
import team.seventhmile.tripforp.domain.place.dto.QGetPlaceCountResponse;
import team.seventhmile.tripforp.domain.place.entity.QPlace;
import team.seventhmile.tripforp.domain.spot.entity.QSpot;

@RequiredArgsConstructor
@Repository
public class SpotRepositoryImpl implements SpotRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QSpot qSpot = QSpot.spot;
    private final QCourse qCourse = QCourse.course;
    private final QPlace qPlace = QPlace.place;

    @Override
    @Cacheable(value = "popularPlaces", key = "#city")
    public List<GetPlaceCountResponse> getPlaceCount(String city, int size) {
        return queryFactory
            .select(new QGetPlaceCountResponse(
                qPlace.id,
                qPlace.mapPlaceId,
                qSpot.count().as("count")
            ))
            .from(qSpot)
            .innerJoin(qSpot.course)
            .innerJoin(qSpot.place)
            .where(qCourse.region.city.eq(city))
            .groupBy(qPlace.id)
            .orderBy(qSpot.count().desc(), qPlace.id.asc())
            .limit(size)
            .fetch();
    }
}
