package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.QGenre;
import com.itwill.lightbooks.domain.QNGenre;
import com.itwill.lightbooks.domain.QNovel;
import com.itwill.lightbooks.dto.NovelSearchDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NovelQuerydslImpl extends QuerydslRepositorySupport 
	implements NovelQuerydsl{

	private final JPAQueryFactory queryFactory;
	
	public NovelQuerydslImpl(JPAQueryFactory queryFactory) {
		super(Novel.class);
		this.queryFactory = queryFactory;
		
	}
	
	

	@Override
	public Novel searchById(Long id) {
		
		QNovel novel = QNovel.novel;
		JPQLQuery<Novel> query = from(novel);
		query.where(novel.id.eq(id));
		Novel entity = query.fetchOne();
		
		return entity;
	}

	@Override
	public Novel searchByIdWithGenre(Long id) {
		QNovel novel = QNovel.novel;
		QNGenre novelGenre = QNGenre.nGenre;
		QGenre genre = QGenre.genre;
		
		
		JPQLQuery<Novel> query = from(novel)
				.leftJoin(novel.novelGenre, novelGenre).fetchJoin()
				.leftJoin(novelGenre.genre, genre).fetchJoin()
				.where(novel.id.eq(id));
		Novel entity = query.fetchOne();
		
	    if (entity == null) {
	        log.error("소설을 찾을 수 없습니다! ID: " + id);
	    } else if (entity.getNovelGenre() == null || entity.getNovelGenre().isEmpty()) {
	        log.warn("소설은 찾았지만 장르가 없습니다! ID: " + id);
	    } else {
	        log.info("소설 ID: " + id + ", 장르 개수: " + entity.getNovelGenre().size());
	    }
		
		
		return entity;
	}

	
	@Override
	public List<Novel> searchByUserIdWithGenre(Long id) {
		QNovel novel = QNovel.novel;
		QNGenre novelGenre = QNGenre.nGenre;
		QGenre genre = QGenre.genre;
		
		JPQLQuery<Novel> query = from(novel)
				.leftJoin(novel.novelGenre, novelGenre).fetchJoin()
				.leftJoin(novelGenre.genre, genre).fetchJoin()
				.where(novel.userId.eq(id))
				.orderBy(novel.id.desc());
		List<Novel> entity = query.fetch();
		
		return entity;
	}

	@Transactional
	@Override
	public void updateNovel(Long novelId, String title, String intro, String coverSrc, Integer ageLimit, Integer state,
			List<String> days, Integer genreId) {
		QNovel qNovel = QNovel.novel;
        QNGenre qNGenre = QNGenre.nGenre;
        
        queryFactory.update(qNovel)
	        .where(qNovel.id.eq(novelId))
	        .set(qNovel.title, title)
	        .set(qNovel.intro, intro)
	        .set(qNovel.coverSrc, coverSrc)
	        .set(qNovel.ageLimit, ageLimit)
	        .set(qNovel.state, state)
	        .set(qNovel.days, days != null ? String.join(",", days) : "비정기")
	        .execute();
        
        if (genreId != null) {
            queryFactory.update(qNGenre)
                    .where(qNGenre.novel.id.eq(novelId))
                    .set(qNGenre.genre.id, genreId.longValue())
                    .execute();
        }
	}


	@Override
	public Page<Novel> searchByKeyword(NovelSearchDto dto, Pageable pageable) {
		log.info("searchByKeyword(dto={}, pageable={})", dto, pageable);
		
		QNovel novel = QNovel.novel;
		JPQLQuery<Novel> query = from(novel);
		BooleanBuilder builder = new BooleanBuilder();
		
		String keyword = dto.getKeyword();
		
		 builder.and(novel.title.containsIgnoreCase(keyword)
	               .or(novel.writer.containsIgnoreCase(keyword)));

		query.where(builder);
		
		// Paging & Sorting 적용 
		getQuerydsl().applyPagination(pageable, query);
		
		
		// query(select 문) 실행
		List<Novel> list = query.fetch(); // -> 한 페이지에 보여줄 컨텐트
		log.info("list size = {}", list.size());
		
		long count = query.fetchCount(); // -> 전체 레코드 개수
		log.info("fetch count = {}", count);
		
		// Page<Novel> 객체 생성
		Page<Novel> result = new PageImpl<Novel>(list, pageable, count);
		
		return result;
	}

	
}
