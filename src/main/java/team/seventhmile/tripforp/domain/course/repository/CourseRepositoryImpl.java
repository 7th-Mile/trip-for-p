package team.seventhmile.tripforp.domain.course.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.course.dto.GetCourseListResponse;
import team.seventhmile.tripforp.domain.course.dto.GetCourseResponse;
import team.seventhmile.tripforp.domain.course.dto.QGetCourseListResponse;
import team.seventhmile.tripforp.domain.course.dto.QGetCourseResponse;
import team.seventhmile.tripforp.domain.course.entity.Course;
import team.seventhmile.tripforp.domain.course.entity.QCourse;
import team.seventhmile.tripforp.domain.courseLike.entity.QCourseLike;
import team.seventhmile.tripforp.domain.region.entity.Province;
import team.seventhmile.tripforp.domain.spot.dto.CourseGetItemDto;
import team.seventhmile.tripforp.domain.spot.dto.QCourseGetItemDto;
import team.seventhmile.tripforp.domain.spot.entity.QSpot;

@RequiredArgsConstructor
@Repository
public class CourseRepositoryImpl implements CourseRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCourse qCourse = QCourse.course;
    private final QSpot qSpot = QSpot.spot;
    private final QCourseLike qCourseLike = QCourseLike.courseLike;

    @Override
    public Page<GetCourseListResponse> getCourses(String keyword, Pageable pageable) {
        List<GetCourseListResponse> courses = queryFactory
            .select(new QGetCourseListResponse(
                qCourse.id,
                qCourse.creator.nickname,
                qCourse.title,
                qCourse.region.province,
                qCourse.region.city,
                qCourse.startDate,
                qCourse.endDate,
                qCourse.createdAt,
                qCourse.views,
                JPAExpressions
                    .select(qCourseLike.count())
                    .from(qCourseLike)
                    .where(qCourseLike.course.eq(qCourse))
            ))
            .from(qCourse)
            .leftJoin(qCourse.creator)
            .leftJoin(qCourse.region)
            .where(eqRegion(keyword.trim()))
            .orderBy(qCourse.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qCourse.count())
            .from(qCourse)
            .where(eqRegion(keyword.trim()));

        return PageableExecutionUtils.getPage(courses, pageable, count::fetchOne);
    }


    private BooleanExpression eqRegion(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        Province province = Province.findByNameOptional(keyword)
            .orElse(null);
        if (province == null) {
            return qCourse.region.city.eq(keyword);
        }
        return qCourse.region.province.eq(province);
    }

    @Override
    public Page<GetCourseListResponse> getMyCourses(String email, Pageable pageable) {
        List<GetCourseListResponse> courses = queryFactory
            .select(new QGetCourseListResponse(
                qCourse.id,
                qCourse.creator.nickname,
                qCourse.title,
                qCourse.region.province,
                qCourse.region.city,
                qCourse.startDate,
                qCourse.endDate,
                qCourse.createdAt,
                qCourse.views,
                JPAExpressions
                    .select(qCourseLike.count())
                    .from(qCourseLike)
                    .where(qCourseLike.course.eq(qCourse))
            ))
            .from(qCourse)
            .where(qCourse.creator.email.eq(email))
            .orderBy(qCourse.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qCourse.count())
            .from(qCourse)
            .where(qCourse.creator.email.eq(email));

        return PageableExecutionUtils.getPage(courses, pageable, count::fetchOne);
    }

    @Override
    public Course findCourse(Long id) {
        return queryFactory
            .selectFrom(qCourse)
            .leftJoin(qCourse.creator).fetchJoin()
            .leftJoin(qCourse.spots, qSpot).fetchJoin()
            .leftJoin(qSpot.place).fetchJoin()
            .leftJoin(qCourse.region).fetchJoin()
            .where(qCourse.id.eq(id))
            .fetchOne();
    }

    @Override
    public GetCourseResponse getCourse(Long id) {
        List<CourseGetItemDto> spots = queryFactory
            .select(new QCourseGetItemDto(
                    qSpot.id,
                    qSpot.place,
                    qSpot.tripDate,
                    qSpot.memo,
                    qSpot.sequence
            ))
            .from(qSpot)
            .leftJoin(qSpot.place)
            .where(qSpot.course.id.eq(id))
            .fetch();

        GetCourseResponse response = queryFactory
            .select(new QGetCourseResponse(
                qCourse.id,
                qCourse.creator.nickname,
                qCourse.title,
                qCourse.region,
                qCourse.startDate,
                qCourse.endDate,
                qCourse.views,
                JPAExpressions
                    .select(qCourseLike.count())
                    .from(qCourseLike)
                    .where(qCourseLike.course.id.eq(qCourse.id)),
                qCourse.createdAt
            ))
            .from(qCourse)
            .where(qCourse.id.eq(id))
            .fetchOne();

        response.setSpots(spots);

        return response;
    }

}
