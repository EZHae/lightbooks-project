package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.domain.QNovel;
import com.itwill.lightbooks.domain.QNovelGradeRequests;
import com.itwill.lightbooks.domain.QUser;
import com.itwill.lightbooks.dto.NovelSearchGradeDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NovelGradeQuerydslImpl extends QuerydslRepositorySupport implements NovelGradeQuerydsl {
	
	public NovelGradeQuerydslImpl() {
		super(NovelGradeQuerydslImpl.class);
	}

	// 프리미엄 신청 리스트들을 검색
	@Override
	public Page<NovelGradeRequest> searchByKeyword(NovelSearchGradeDto dto, Pageable pageable) {
		log.info("searchByKeyword()");
		QNovel novel = QNovel.novel;
		QUser user = QUser.user;
		QNovelGradeRequests ngReq = QNovelGradeRequests.novelGradeRequests;
		
		JPQLQuery<NovelGradeRequest> query = from(ngReq)
				.join(ngReq.user, user)
				.join(ngReq.novel, novel)
				.select(ngReq);
		BooleanBuilder builder = new BooleanBuilder();
		
		String keyword = dto.getKeyword();
		
		if(ngReq.getType() != null) {
			builder.and(builder);
		}
		
		builder.and(user.loginId.containsIgnoreCase(keyword))
				.or(novel.title.containsIgnoreCase(keyword))
				.or(ngReq.type);
		
		query.where(builder);
		
		getQuerydsl().applyPagination(pageable, query);
		
		// query(select 문) 실행
		List<NovelGradeRequest> list = query.fetch(); // -> 한 페이지에 보여줄 컨텐트
		long count = query.fetchCount(); // -> 전체 레코드 개수
		
		// Page<Novel> 객체 생성
		Page<NovelGradeRequest> result = new PageImpl<NovelGradeRequest>(list, pageable, count);
		
		return result;
	}

}
