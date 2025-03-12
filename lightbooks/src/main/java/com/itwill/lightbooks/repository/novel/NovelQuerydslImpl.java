package com.itwill.lightbooks.repository.novel;

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
	public Novel searchById(Integer id) {
		
		QNovel novel = QNovel.novel;
		JPQLQuery<Novel> query = from(novel);
		query.where(novel.id.eq(id));
		Novel entity = query.fetchOne();
		
		return entity;
	}

	@Override
	public Novel searchByIdWithGenre(Integer id) {
		QNovel novel = QNovel.novel;
		QNGenre novelGenre = QNGenre.nGenre;
		QGenre genre = QGenre.genre;
		
		JPQLQuery<Novel> query = from(novel)
				.leftJoin(novel.novelGenre, novelGenre).fetchJoin()
				.leftJoin(novelGenre.genre, genre).fetchJoin()
				.where(novel.id.eq(id));
		Novel entity = query.fetchOne();
		
		return entity;
	}

	
}
