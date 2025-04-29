package team.seventhmile.tripforp.domain.freePost.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.freePost.entity.FreePost;
import team.seventhmile.tripforp.domain.freePost.entity.QFreePost;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class FreePostRepositoryImpl implements FreePostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QFreePost qFreePost = QFreePost.freePost;

    @Override
    public Page<FreePost> getFreePosts(Pageable pageable) {
        List<FreePost> freePosts = queryFactory.selectFrom(qFreePost)
            .orderBy(qFreePost.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qFreePost.count())
            .from(qFreePost);

        return PageableExecutionUtils.getPage(freePosts, pageable, count::fetchOne);

    }

    @Override
    public Page<FreePost> getFreePostKeywordContaining(String keyword, Pageable pageable) {
        BooleanExpression searchKeyword = qFreePost.content.containsIgnoreCase(keyword);

        List<FreePost> freePosts = queryFactory.selectFrom(qFreePost)
            .where(searchKeyword)
            .orderBy(qFreePost.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qFreePost.count())
            .from(qFreePost)
            .where(searchKeyword);

        return PageableExecutionUtils.getPage(freePosts, pageable, count::fetchOne);
    }


}
