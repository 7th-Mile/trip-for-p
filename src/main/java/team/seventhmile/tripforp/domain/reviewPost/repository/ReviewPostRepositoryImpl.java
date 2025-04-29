package team.seventhmile.tripforp.domain.reviewPost.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.course.entity.QCourse;
import team.seventhmile.tripforp.domain.reviewPost.entity.QReviewPost;
import team.seventhmile.tripforp.domain.reviewPost.entity.ReviewPost;

@RequiredArgsConstructor
@Repository
public class ReviewPostRepositoryImpl implements ReviewPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReviewPost qReviewPost = QReviewPost.reviewPost;
    private final QCourse qCourse = QCourse.course;

    @Override
    public Page<ReviewPost> getReviewPosts(Pageable pageable) {
        List<ReviewPost> reviewPosts = queryFactory.selectFrom(qReviewPost)
            .orderBy(qReviewPost.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qReviewPost.count())
            .from(qReviewPost);

        return PageableExecutionUtils.getPage(reviewPosts, pageable, count::fetchOne);

    }

    @Override
    public Page<ReviewPost> getReviewPostKeywordContaining(String keyword, Pageable pageable) {
        BooleanExpression searchKeyword = qReviewPost.title.containsIgnoreCase(keyword)
            .or(qReviewPost.content.containsIgnoreCase(keyword));

        List<ReviewPost> reviewPosts = queryFactory.selectFrom(qReviewPost)
            .where(searchKeyword)
            .orderBy(qReviewPost.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qReviewPost.count())
            .from(qReviewPost)
            .where(searchKeyword);

        return PageableExecutionUtils.getPage(reviewPosts, pageable, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> getMyReviews(String email, Pageable pageable) {
        List<ReviewPost> myReview = queryFactory
            .selectFrom(qReviewPost)
            .where(qReviewPost.user.email.eq(email))
            .orderBy(qReviewPost.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();
        JPAQuery<Long> countQuery = queryFactory
            .select(qReviewPost.count())
            .from(qReviewPost)
            .where(qReviewPost.user.email.eq(email));
        return PageableExecutionUtils.getPage(myReview, pageable, countQuery::fetchOne);
    }
}
