package com.itwill.lightbooks.repository.novel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.QGenre;
import com.itwill.lightbooks.domain.QNGenre;
import com.itwill.lightbooks.domain.QNovel;
import com.itwill.lightbooks.dto.NovelListItemDto;
import com.itwill.lightbooks.dto.NovelSearchDto;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NovelQuerydslImpl extends QuerydslRepositorySupport 
	implements NovelQuerydsl{

	private final JPAQueryFactory queryFactory;
	private final EpisodeRepository episodeRepo;
	
	public NovelQuerydslImpl(JPAQueryFactory queryFactory, EpisodeRepository episodeRepo) {
		super(Novel.class);
		this.queryFactory = queryFactory;
		this.episodeRepo = episodeRepo;
		
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
		
		if("t".equals(dto.getCategory())) {
			builder.and(novel.title.containsIgnoreCase(keyword));
			
		} else if ("w".equals(dto.getCategory())) {
			builder.and(novel.writer.containsIgnoreCase(keyword));
		}

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

	
	// 베스트 소설 조회
	@Override
	public List<NovelListItemDto> findRandomBestNovels(int count) {
		QNovel novel = QNovel.novel;
		
		// 조건이 좋아요 100이상, 평점 4.0이상, 조회수 1000이상
		BooleanExpression condition = novel.likeCount.goe(100)
				.and(novel.rating.goe(BigDecimal.valueOf(4.0)));
		
		// 조건에 맞는 소설 랜덤 조회
		List<Novel> novels = queryFactory.selectFrom(novel)
				.where(condition)
				.orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc()) // function('rand')는 DB에서 랜덤 정렬
				.limit(count)
				.fetch();
		
		// id 
		List<Long> novelIds = novels.stream()
				.map(Novel :: getId)
				.collect(Collectors.toList());
		
		// 조회수
		Map<Long, Long> viewsMap = episodeRepo.getTotalViewsByNovelIds(novelIds);
		
		// DTO로 변환해서 조회수 포함시켜 반환
		return novels.stream()
				.map(n -> NovelListItemDto.fromEntity(n, viewsMap.getOrDefault(n.getId(), 0L)))
				.collect(Collectors.toList());
	}


	// 무료 소설 조회
	@Override
	public List<Novel> findFreeNovels(int limit) {
		QNovel novel = QNovel.novel;
		
	    return queryFactory
	            .selectFrom(novel)
	            .where(novel.grade.eq(0)) // 무료
	            .orderBy(novel.createdTime.desc()) // 최신순
	            .limit(limit)
	            .fetch();
		
	}

	// 유료 소설 조회
	@Override
	public List<Novel> findPaidNovels(int limit) {
		QNovel novel = QNovel.novel;
		
	    return queryFactory
	            .selectFrom(novel)
	            .where(novel.grade.eq(1)) // 유료
	            .orderBy(novel.createdTime.desc()) // 최신순
	            .limit(limit)
	            .fetch();
	}


	// 장르별 소설 조회
	@Override
	public List<Novel> findNovelsByGenreName(String genreName, int limit) {
	    QNovel novel = QNovel.novel;
	    QNGenre nGenre = QNGenre.nGenre;
	    QGenre genre = QGenre.genre;
	    
	    return queryFactory
	            .selectFrom(novel)
	            .join(novel.novelGenre, nGenre)
	            .join(nGenre.genre, genre)
	            .where(genre.name.eq(genreName))
	            .orderBy(novel.createdTime.desc())
	            .limit(limit)
	            .fetch();
	}



	@Override
	public List<Novel> findRandomNovels(int limit) {
		QNovel novel = QNovel.novel;
		return queryFactory
				.selectFrom(novel)
				.orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
				.limit(limit)
				.fetch();
	}


	// 여기서 부터 무료
	// 추천 신작 무료 소설 (최근순)
	@Override
	public List<NovelListItemDto> findByFreeGradeOrderByNew(int limit) {
		QNovel novel = QNovel.novel;
		
		List<Novel> novels = queryFactory
				.selectFrom(novel)
				.where(novel.grade.eq(0))
				.orderBy(novel.likeCount.desc(), novel.rating.desc(), novel.modifiedTime.desc())
				.limit(limit)
				.fetch();
		
		return toDtoList(novels);
	}
	
	// 인기 연재 무료 소설 (좋아요/평점순)
	@Override
	public List<NovelListItemDto> findByFreeGradeAndSerialOrderByPopularity(int limit) {
		QNovel novel = QNovel.novel;
		
		List<Novel> novels = queryFactory
				.selectFrom(novel)
				.where(novel.grade.eq(0).and(novel.state.eq(1)))
				.orderBy(novel.likeCount.desc(), novel.rating.desc())
				.limit(limit)
				.fetch();
				
		return toDtoList(novels);
	}
	
	// 인기 완결 무료 소설
	@Override
	public List<NovelListItemDto> findByFreeGradeAndCompletedOrderByPopularity(int limit) {
		QNovel novel = QNovel.novel;
		
		List<Novel> novels = queryFactory
				.selectFrom(novel)
				.where(novel.grade.eq(0).and(novel.state.eq(0)))
				.orderBy(novel.createdTime.desc())
				.limit(limit)
				.fetch();
				
		return toDtoList(novels);
	}
	
	// 무료 이벤트 소설
	@Override
	public List<NovelListItemDto> findByFreeGradeEventOrderByNew(int limit) {
		QNovel novel = QNovel.novel;
		
		List<Novel> novels = queryFactory
				.selectFrom(novel)
				.where(novel.grade.eq(0)) // 추후에 .where(novel.grade.eq(0).and(novel.isEvent.isTrue())) 이벤트
				.orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc()) 
				.limit(limit)
				.fetch();
		
		return toDtoList(novels);
	}

	// 무료 장르별 소설 (장르 지정)
	@Override
	public List<NovelListItemDto> findByFreeGradeAndGenreRandom(String genreName, int limit) {
		QNovel novel = QNovel.novel;
	    QNGenre nGenre = QNGenre.nGenre;
	    QGenre genre = QGenre.genre;
	    
	    List<Novel> novels = queryFactory
	            .selectFrom(novel)
	            .join(novel.novelGenre, nGenre)
	            .join(nGenre.genre, genre)
	            .where(genre.name.eq(genreName).and(novel.grade.eq(0)))
	            .orderBy(novel.createdTime.desc())
	            .limit(limit)
	            .fetch();
		
		return toDtoList(novels);
	}
	// DTO 변환
	private List<NovelListItemDto> toDtoList(List<Novel> novels) {
		return novels.stream()
				.map(NovelListItemDto::fromEntity) // 단순히 변환
				.toList();
	}

	@Override
	public List<Novel> findFreeOrderByLikeDesc() {
		QNovel novel = QNovel.novel;
		return queryFactory
				.selectFrom(novel)
				.where(novel.grade.eq(0))
				.orderBy(novel.likeCount.desc(), novel.rating.desc())
				.fetch();
	}			
	
}
