package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.domain.QNovel;
import com.itwill.lightbooks.domain.QNovelGradeRequest;
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
		QNovelGradeRequest ngReq = QNovelGradeRequest.novelGradeRequest;
		
		String keyword = dto.getKeyword();
		String category = dto.getCategory();
		
		
		Integer typeStr = convertType(keyword);
		Integer statusStr = convertStatus(keyword);
		
		JPQLQuery<NovelGradeRequest> query = from(ngReq)
				.join(ngReq.user, user)
				.join(ngReq.novel, novel)
				.select(ngReq);
		
		// 동적으로 where 구문(조건절)을 생성하기 위해서 BooleanBuilder 객체를 생성 
		BooleanBuilder builder = new BooleanBuilder();
		
		switch(category) {
		case "id":
			builder.and(user.loginId.containsIgnoreCase(keyword));
			break;
		case "t":
			builder.and(novel.title.containsIgnoreCase(keyword));
			break;
		case "ty":
			builder.or(ngReq.type.eq(typeStr));
			break;
		case "st":
			builder.or(ngReq.status.eq(statusStr));
			break;
		}
		
		query.where(builder);
		
		getQuerydsl().applyPagination(pageable, query);
		
		// query(select 문) 실행
		List<NovelGradeRequest> list = query.fetch(); // -> 한 페이지에 보여줄 컨텐트
		long count = query.fetchCount(); // -> 전체 레코드 개수
		
		// Page<Novel> 객체 생성
		Page<NovelGradeRequest> result = new PageImpl<NovelGradeRequest>(list, pageable, count);
		
		return result;
	}

	
	// Type 을 String 으로 변환
	private Integer convertType(String typeStr) {
		if("무료".equals(typeStr)) 
			return 0;
		if("유료".equals(typeStr))
			return 1;
		return null; 
	}
	// status 를 String 으로 변환
	private Integer convertStatus(String statusStr) {
		if("대기".equals(statusStr))
			return 0;
		if("승인".equals(statusStr))
			return 1;
		if("취소".equals(statusStr))
			return 2;
		return null;
	}
	
}
