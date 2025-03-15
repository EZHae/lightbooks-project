package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.QGenre;
import com.itwill.lightbooks.domain.QNGenre;
import com.itwill.lightbooks.domain.QNovel;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NovelQuerydslImpl extends QuerydslRepositorySupport 
	implements NovelQuerydsl{

	public NovelQuerydslImpl() {
		super(Novel.class);
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
	
}
