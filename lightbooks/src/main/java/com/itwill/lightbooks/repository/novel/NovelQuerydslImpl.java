package com.itwill.lightbooks.repository.novel;

import java.math.BigDecimal;
import java.util.Collections;
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
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

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
	public Map<Long, String> getGenreNamesByNovelIds(List<Long> novelIds) {
	    QNovel novel = QNovel.novel;
	    QNGenre novelGenre = QNGenre.nGenre;
	    QGenre genre = QGenre.genre;

	    if (novelIds == null || novelIds.isEmpty()) {
	        return Collections.emptyMap();
	    }

	    List<Tuple> result = queryFactory
	            .select(novel.id, genre.name)
	            .from(novel)
	            .join(novelGenre).on(novelGenre.novel.eq(novel))
	            .join(genre).on(novelGenre.genre.eq(genre))
	            .where(novel.id.in(novelIds))
	            .fetch();

	    return result.stream().collect(Collectors.toMap(
	            tuple -> tuple.get(novel.id),
	            tuple -> tuple.get(genre.name)
	    ));
	}

	// 각 소설 검색 기능
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


	// ================================== 메인 홈 =================================
	// 베스트 소설 조회
	@Override
	public List<NovelListItemDto> findRandomBestNovels(int count) {
		QNovel novel = QNovel.novel;
	    QNGenre nGenre = QNGenre.nGenre;
	    QGenre genre = QGenre.genre;
		
		// 조건이 좋아요 100이상, 평점 4.0이상
		BooleanExpression condition = novel.likeCount.goe(100)
				.and(novel.rating.goe(BigDecimal.valueOf(4.0)));
		
		
		// 아이디 먼저 조회
		List<Long> ids = queryFactory.select(novel.id)
				.from(novel)
				.where(condition)
				.fetch();
		
		// 셔플하고 랜덤 일부 추출
		Collections.shuffle(ids);
		List<Long> selectedIds = ids.stream().limit(count).toList();
		
		// 조건에 맞는 소설 + 장르 조회
		List<Novel> novels = queryFactory.selectFrom(novel)
				.leftJoin(novel.novelGenre, nGenre).fetchJoin()
				.leftJoin(nGenre.genre, genre).fetchJoin()
				.where(novel.id.in(selectedIds))
				.fetch();
		
		// 조회수
		Map<Long, Long> viewsMap = episodeRepo.getTotalViewsByNovelIds(selectedIds);
		
		// DTO로 변환해서 조회수 포함시켜 반환
		return toDtoListFull(novels, viewsMap);
	}


	// 무료 소설 조회
	@Override
	public List<NovelListItemDto> findFreeNovels(int limit) {
		QNovel novel = QNovel.novel;

	    List<Novel> novels = queryFactory
	        .selectFrom(novel)
	        .where(novel.grade.eq(0))
	        .orderBy(novel.createdTime.desc())
	        .limit(limit)
	        .fetch();

	    return toDtoList(novels);
	}

	// 유료 소설 조회
	@Override
	public List<NovelListItemDto> findPaidNovels(int limit) {
		QNovel novel = QNovel.novel;

	    List<Novel> novels = queryFactory
	        .selectFrom(novel)
	        .where(novel.grade.eq(1))
	        .orderBy(novel.createdTime.desc())
	        .limit(limit)
	        .fetch();

	    return toDtoList(novels);
	}


	@Override
	public List<NovelListItemDto> findNovelsByGenreName(String genreName, int limit) {
	    QNovel novel = QNovel.novel;
	    QNGenre nGenre = QNGenre.nGenre;
	    QGenre genre = QGenre.genre;

	    // genre.name까지 select
	    List<Tuple> results = queryFactory
	        .select(novel, genre.name)
	        .from(novel)
	        .join(novel.novelGenre, nGenre).fetchJoin()
	        .join(nGenre.genre, genre).fetchJoin()
	        .where(genre.name.eq(genreName))
	        .orderBy(novel.createdTime.desc())
	        .limit(limit)
	        .fetch();

	    return results.stream()
	        .map(tuple -> NovelListItemDto.fromEntity(
	            tuple.get(novel),
	            tuple.get(genre.name), // genreName 전달!
	            0L // 조회수 없으면 0L
	        ))
	        .toList();
	}
	
	@Override
	public List<NovelListItemDto> findRandomNovels(int limit) {
	    QNovel novel = QNovel.novel;

	    List<Long> allIds = queryFactory	
	        .select(novel.id)
	        .from(novel)
	        .fetch();

	    if (allIds.isEmpty()) {
	        return Collections.emptyList();
	    }

	    Collections.shuffle(allIds);

	    List<Long> selectedIds = allIds.stream()
	        .limit(limit)
	        .toList();

	    List<Novel> novels = queryFactory
	        .selectFrom(novel)
	        .where(novel.id.in(selectedIds))
	        .fetch();

	    return toDtoList(novels);
	}

	// 추천 - 베스트 탭
	@Override
	public List<NovelListItemDto> findAllOrderByLikeDesc(int limit) {
	    QNovel novel = QNovel.novel;

	    List<Novel> novels = queryFactory
	        .selectFrom(novel)
	        .orderBy(novel.likeCount.desc(), novel.rating.desc())
	        .limit(limit)
	        .fetch();

	    return novels.stream()
	        .map(n -> NovelListItemDto.fromEntity(n, "", 0L))
	        .toList();
	}
	
	

	// ========================= 여기서 부터 무료/유료  =========================
	// 추천 신작 소설 (최근순)
	@Override
	public List<NovelListItemDto> findByGradeOrderByNew(int grade, int limit) {
	    QNovel novel = QNovel.novel;
	    QNGenre nGenre = QNGenre.nGenre;
	    QGenre genre = QGenre.genre;
		
		List<Novel> novels = queryFactory
				.selectFrom(novel)
				.leftJoin(novel.novelGenre, nGenre).fetchJoin()
		        .leftJoin(nGenre.genre, genre).fetchJoin()
		        .where(novel.grade.eq(grade))
		        .orderBy(novel.createdTime.desc())
		        .limit(limit)
		        .fetch();
		
		// 조회수 
		List<Long> novelIds = novels.stream().map(Novel::getId).toList();
		Map<Long, Long> viewsMap = episodeRepo.getTotalViewsByNovelIds(novelIds);
		return toDtoListFull(novels, viewsMap);
	}
	
	// 인기 연재 소설 (좋아요/평점순)
	@Override
	public List<NovelListItemDto> findByGradeAndSerialOrderByPopularity(int grade,int state,int limit) {
		QNovel novel = QNovel.novel;
		
		List<Novel> novels = queryFactory
				.selectFrom(novel)
				.where(novel.grade.eq(grade).and(novel.state.eq(state)))
				.orderBy(novel.likeCount.desc(), novel.rating.desc())
				.limit(limit)
				.fetch();
				
		return toDtoList(novels);
	}
	
	// 인기 완결 소설
	@Override
	public List<NovelListItemDto> findByGradeAndCompletedOrderByPopularity(int grade,int state ,int limit) {
		QNovel novel = QNovel.novel;
		
		List<Novel> novels = queryFactory
				.selectFrom(novel)
				.where(novel.grade.eq(grade).and(novel.state.eq(state)))
				.orderBy(novel.createdTime.desc())
				.limit(limit)
				.fetch();
				
		return toDtoList(novels);
	}
	
	// 이벤트 소설
	@Override
	public List<NovelListItemDto> findByGradeEventOrderByNew(int grade, int limit) {
		QNovel novel = QNovel.novel;
		
		List<Novel> novels = queryFactory
				.selectFrom(novel)
				.where(novel.grade.eq(grade)) // 추후에 .where(novel.grade.eq(grade).and(novel.isEvent.isTrue())) 이벤트
				.orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc()) 
				.limit(limit)
				.fetch();
		
		return toDtoList(novels);
	}
	// 장르별 소설 (장르 지정)
	@Override
	public List<NovelListItemDto> findByGradeAndGenreRandom(int grade, String genreName, int limit) {
		QNovel novel = QNovel.novel;
	    QNGenre nGenre = QNGenre.nGenre;
	    QGenre genre = QGenre.genre;
	    
	    List<Novel> novels = queryFactory
	            .selectFrom(novel)
	            .join(novel.novelGenre, nGenre)
	            .join(nGenre.genre, genre)
	            .where(genre.name.eq(genreName).and(novel.grade.eq(grade)))
	            .orderBy(novel.createdTime.desc())
	            .limit(limit)
	            .fetch();
		
		return toDtoList(novels);
	}
	
	@Override
	public List<NovelListItemDto> findOrderByLikeDesc(int grade, int limit) {
	    QNovel novel = QNovel.novel;

	    List<Novel> novels = queryFactory
	        .selectFrom(novel)
	        .where(novel.grade.eq(grade))
	        .orderBy(novel.likeCount.desc(), novel.rating.desc())
	        .limit(limit)
	        .fetch();

	    return novels.stream()
	        .map(n -> NovelListItemDto.fromEntity(n, "", 0L))
	        .toList();
	}
	
	
	@Override
	public List<NovelListItemDto> findByGradeAndGenre(int grade, String genreName, int limit) {
		QNovel novel = QNovel.novel;
	    QNGenre nGenre = QNGenre.nGenre;
	    QGenre genre = QGenre.genre;
	    
	    List<Novel> novels = queryFactory
	            .selectFrom(novel)
	            .join(novel.novelGenre, nGenre)
	            .join(nGenre.genre, genre)
	            .where(genre.name.eq(genreName).and(novel.grade.eq(grade)))
	            .orderBy(novel.modifiedTime.desc(), novel.rating.desc(), novel.likeCount.desc())
	            .limit(limit)
	            .fetch();
		
		return toDtoList(novels);
	}			
	
	// DTO 변환
	private List<NovelListItemDto> toDtoList(List<Novel> novels) {
	    return novels.stream()
	            .map(novel -> NovelListItemDto.fromEntity(novel, "", 0L))
	            .toList();
	}
	
	// 조회수 포함 + 장르 포함
	private List<NovelListItemDto> toDtoListFull(List<Novel> novels, Map<Long, Long> viewsMap) {
	    return novels.stream()
	            .map(novel -> {
	            	String genres =  novel.getNovelGenre().stream()
	            			.map(nGenre -> nGenre.getGenre().getName())
	            			.collect(Collectors.joining(", "));
	            	
	            	return NovelListItemDto.fromEntity(
		                novel, 
		                genres, // 장르 정보가 없을 때는 빈 문자열 처리!
		                viewsMap.getOrDefault(novel.getId(), 0L)
	            		);
	            })
	            .toList();
	}
}
