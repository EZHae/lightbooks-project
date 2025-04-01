package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.domain.QNovel;
import com.itwill.lightbooks.domain.QNovelGradeRequest;
import com.itwill.lightbooks.domain.QUser;
import com.itwill.lightbooks.dto.NovelSearchGradeDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NovelGradeQuerydslImpl extends QuerydslRepositorySupport implements NovelGradeQuerydsl {
	
	private final JPAQueryFactory queryFactory;
	
	public NovelGradeQuerydslImpl(JPAQueryFactory queryFactory) {
		super(NovelGradeQuerydslImpl.class);
		this.queryFactory = queryFactory;
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
	
	@Override
	public Page<NovelGradeRequest> findAllWithUserAndNovel(int page, int size) {
		QNovel novel = QNovel.novel;
		QUser user = QUser.user;
		QNovelGradeRequest r = QNovelGradeRequest.novelGradeRequest;
		page = Math.max(0, page);page = Math.max(0, page);
		// 소설 신청 목록을 user, novel과 함께 fetchJoin으로 한 번에 가져오기
		List<NovelGradeRequest> result = queryFactory
				.selectFrom(r)
				.join(r.user, user).fetchJoin()
				.join(r.novel, novel).fetchJoin()
				.orderBy(r.createdTime.desc())
				.offset(page * size)
				.limit(size)
				.fetch();
		
		// 전체 개수 쿼리
		Long total = queryFactory
				.select(r.count())
				.from(r)
				.fetchOne();
		
		// Page 객체로 변환해서 반환
		Pageable pageable = PageRequest.of(page, size);
		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public Page<NovelGradeRequest> findByStatusWithUserAndNovel(int status, int page, int size) {
		QNovel novel = QNovel.novel;
		QUser user = QUser.user;
		QNovelGradeRequest r = QNovelGradeRequest.novelGradeRequest;
		page = Math.max(0, page);
		//조건(status)에 맞는 신청만 user, novel과 함께 가져옴
		List<NovelGradeRequest> result = queryFactory
				.selectFrom(r)
				.join(r.user, user).fetchJoin()
				.join(r.novel, novel).fetchJoin()
				.where(r.status.eq(status))
				.orderBy(r.createdTime.desc())
				.offset(page * size)
				.limit(size)
				.fetch();
		// 조건에 맞는 신청 개수 (카운트)
		Long total = queryFactory
				.select(r.count())
				.from(r)
				.where(r.status.eq(status))
				.fetchOne();
		
		// 페이지로 변환
		Pageable pageable = PageRequest.of(page, size);
		return new PageImpl<>(result, pageable, total);
	}
	
}
