package team.seventhmile.tripforp.domain.magazine.repository;

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
import team.seventhmile.tripforp.domain.file.entity.QMagazineFile;
import team.seventhmile.tripforp.domain.magazine.dto.GetMagazinesResponse;
import team.seventhmile.tripforp.domain.magazine.dto.QGetMagazinesResponse;
import team.seventhmile.tripforp.domain.magazine.entity.QMagazine;

@RequiredArgsConstructor
@Repository
public class MagazineRepositoryImpl implements MagazineRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QMagazine qMagazine = QMagazine.magazine;
	private final QMagazineFile qFile = QMagazineFile.magazineFile;

	@Override
	public Page<GetMagazinesResponse> getMagazineKeywordContaining(String keyword, Pageable pageable) {
		List<GetMagazinesResponse> magazines = queryFactory
			.select(new QGetMagazinesResponse(
				qMagazine.id,
				qMagazine.title,
				qMagazine.content,
				qMagazine.views,
				qMagazine.createdAt,
				qFile.url
			))
			.from(qMagazine)
			.leftJoin(qFile)
			.on(qMagazine.id.eq(qFile.magazine.id),
				qFile.id.eq(JPAExpressions
					.select(qFile.id.min())
					.from(qFile)
					.where(qFile.magazine.id.eq(qMagazine.id))))
			.where(containsTitle(keyword))
			.orderBy(qMagazine.createdAt.desc())
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(qMagazine.count())
			.from(qMagazine)
			.where(containsTitle(keyword));

		return PageableExecutionUtils.getPage(magazines, pageable, countQuery::fetchOne);
	}

	private BooleanExpression containsTitle(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return null;
		}
		return qMagazine.title.contains(keyword.trim());
	}
}
