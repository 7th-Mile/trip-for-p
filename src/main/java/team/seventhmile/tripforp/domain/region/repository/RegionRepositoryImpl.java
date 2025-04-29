package team.seventhmile.tripforp.domain.region.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.course.entity.QCourse;
import team.seventhmile.tripforp.domain.region.dto.GetPopularRegionsResponse;
import team.seventhmile.tripforp.domain.region.dto.QGetPopularRegionsResponse;
import team.seventhmile.tripforp.domain.region.entity.QRegion;

@Repository
@RequiredArgsConstructor
public class RegionRepositoryImpl implements RegionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QRegion qRegion = QRegion.region;
    private final QCourse qCourse = QCourse.course;

    @Override
    public List<GetPopularRegionsResponse> getPopularRegions(int size) {
        return queryFactory
            .select(new QGetPopularRegionsResponse(
            qRegion.id,
            qRegion.province,
            qRegion.city,
            qRegion.id.count()
        ))
            .from(qRegion)
            .innerJoin(qCourse)
            .on(qCourse.region.id.eq(qRegion.id))
            .groupBy(qRegion.id)
            .orderBy(qRegion.id.count().desc())
            .limit(size)
            .fetch();
    }
}
