package com.itwill.lightbooks.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Genre;
import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.dto.NovelCreateDto;
import com.itwill.lightbooks.dto.NovelItemDto;
import com.itwill.lightbooks.dto.NovelListItemDto;
import com.itwill.lightbooks.dto.NovelResponseDto;
import com.itwill.lightbooks.dto.NovelSearchDto;
import com.itwill.lightbooks.dto.NovelUpdateDto;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.itwill.lightbooks.repository.genre.GenreRepository;
import com.itwill.lightbooks.repository.novel.NGenreRepository;
import com.itwill.lightbooks.repository.novel.NovelGradeRequestRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NovelService {
	
	private final NovelRepository novelRepo;
	private final GenreRepository genreRepo;
	private final NGenreRepository ngenreRepo;
	private final NovelGradeRequestRepository novelGradeRequestRepo;
	private final EpisodeRepository episodeRepo;
	
	// 작품 수정 페이지에 데이터를 가져옴
	public Novel searchByIdWithGenre(Long id) {
		Novel novel = novelRepo.searchByIdWithGenre(id);
		return novel;
	}
	
	 // 내 작품 페이지에 유저 아이디로 데이터를 가져옴
	   public List<NovelResponseDto> getNovelByUserId(Long userId) {
	      List<Novel> novels = novelRepo.searchByUserIdWithGenre(userId);
	      
	      log.info("해당 유저 소설 : {}", novels);
	      
	      List<Long> novelIds = novels.stream()
	    		  .map(Novel::getId).collect(Collectors.toList());
	      
	      // 회차 조회수 Map 가져오기
	      Map<Long, Long> totalViewsMap = episodeRepo.getTotalViewsByNovelIds(novelIds);
	      
	      return novels.stream().map(novel -> new NovelResponseDto(
	            novel.getId(),
	            novel.getTitle(),
	            novel.getIntro(),
	            novel.getWriter(),
	            novel.getCoverSrc(),
	            novel.getLikeCount(),
	            novel.getState(),
	            novel.getGrade(),
	            novel.getNovelGenre()
	            .stream().map(novelGenre -> novelGenre.getGenre().getName())
	            .collect(Collectors.toList()),
	            novel.getRating(), // 데시멀이라서 stream할 필요없음
	            totalViewsMap.getOrDefault(novel.getId(), 0L)
	            )).collect(Collectors.toList());
	   }
	
	// 소설 하나만 가져오는 기능
	public Novel searchById(Long id) {
		log.info("searchId()");
		Novel novel = novelRepo.findById(id).orElseThrow();
		
		String genre = novel.getNovelGenre().isEmpty() ? "장르 없음" : novel.getNovelGenre().get(0).getGenre().getName();
		log.info("장르 : {}", genre);
		return novel;
	}
	
	// 모든 소설 리스트
	public List<Novel> searchAll() {
		log.info("searchAll()");
		List<Novel> list = novelRepo.findAll();
		
		log.info("search list = {}", list);
		
		return list;
	}
	
	// 소설 생성
	@Transactional
	public Novel create(NovelCreateDto dto) {
		
		// 소설 저장
		Novel novel = novelRepo.save(dto.toEntity());
		log.info("저장되는 소설 = {}", novel);
		
		log.info("받은 장르 ID 목록 = {}", dto.getGenre()); // 입력 확인
		// 장르 정보 가져오기
		List<Genre> selectedGenres = genreRepo.findAllById(dto.getGenre());
		log.info("총 장르 genres = {}",selectedGenres);
		
		// 소설 장르 저장
		List<NGenre> novelGenres = selectedGenres.stream()
				.map(genre -> NGenre.builder()
						.novel(novel)
						.genre(genre)
						.isMain(1)
						.build())
				.collect(Collectors.toList());
		
		ngenreRepo.saveAll(novelGenres);
		log.info("해당 소설 장르 = {}", novelGenres);
		
		return novel;
	}
	
	// 소설 삭제
	@Transactional
	public void deleteById(Long id) {
		novelRepo.deleteById(id);
	}

	public void updateNovel(NovelUpdateDto dto) {
		novelRepo.updateNovel(dto.getId(), dto.getTitle(), dto.getIntro(), dto.getCoverSrc(), dto.getAgeLimit(),
				dto.getState(), dto.getDays(), dto.getGenreId());
		
		log.info("genreID : {}", dto.getGenreId());
	}

	// 제목, 작성자로 소설 검색과 페이징
	public Page<NovelListItemDto> search(NovelSearchDto dto, Sort sort) {
		
		Pageable pageable = PageRequest.of(dto.getP(), 8, sort);
		Page<Novel> result = novelRepo.searchByKeyword(dto , pageable);
		
		 //소설 ID 목록 추출
		List<Long> novelIds = result.stream()
				.map(Novel::getId)
				.collect(Collectors.toList());
		
		//소설별 총 조회수 Map 조회
		Map<Long, Long> viewsMap = episodeRepo.getTotalViewsByNovelIds(novelIds);
		
		return result.map(novel -> NovelListItemDto.fromEntity(
				novel, "", viewsMap.getOrDefault(novel.getId(), 0L)));
	}
	
	//추가
	// 소설 작성자 여부 확인 메서드
    public boolean isUserOwnerOfNovel(Long novelId, Long userId) {
        Novel novel = novelRepo.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 소설을 찾을 수 없습니다. ID: " + novelId));

        // Novel의 userId와 현재 사용자의 userId 비교
        return novel.getUserId().equals(userId);
    }

    public String getMainGenre(Novel novel) {
   	return novel.getNovelGenre()
    			.stream()
    			.filter(novelGenre -> novelGenre.getIsMain() == 1)
    			.map(novelGenre -> novelGenre.getGenre().getName())
    			.findFirst()
    			.orElse("장르없음");
    }

    // 등급 : 유료/무료를 업데이트
    @Transactional
	public void updateNovelGrade(Long novelId, Integer grade) {
		Novel novelUpdateGrade = novelRepo.findById(novelId).orElseThrow();
		novelUpdateGrade.updateGrade(grade);
	}

    
    // 소설 사용자가 해당 소설을 유료 신청되는 NovelGradeRequest 테이블에 데이터를 저장
    @Transactional
	public NovelGradeRequest saveGradeRequest(NovelGradeRequest ngReq) {
		NovelGradeRequest novelGradeRequest = novelGradeRequestRepo.save(ngReq);
		log.info("유료 신청 : {} ", novelGradeRequest);
		return novelGradeRequest;
	}

    
    // 마일리지 샵에서 사용함
    public List<NovelItemDto> getPaidNovelByKeyword(String keyword) {
    	List<Novel> novels = novelRepo.searchPaidAndKeyword(keyword);
    	
    	List<NovelItemDto> result = novels.stream().map(NovelItemDto::fromEntity).toList();
    	
    	return result;
    }

    // 유저
	public Map<Long, String> getUserPremiumStatus(Long userId) {
		List<NovelGradeRequest> result = novelGradeRequestRepo.findByUserId(userId);
		log.info("유저 아이디 : {}", userId);
		return result.stream()
				.filter(req -> req.getStatus() != 2) // 거절은 무시
				.collect(Collectors.toMap(
						req -> req.getNovel().getId(),
						req -> req.getStatus() == 0 ? "대기중" : "완료",
								(existing, replacement) -> existing)); // 중복 시 기존 값 유지
	}
	
    public boolean canApplyForPremiun(Long novelId) {
    	// 소설 정보에 좋아요, 평점.
    	Novel novel = novelRepo.searchById(novelId);
    	
    	// 해당 소설의 회차 데이터를 가져와야함
    	Long episodeCount = episodeRepo.countNovelId(novelId);
    	
    	// 데시멀은 eq가 아닌 compartTo를 사용해야함 소수점까지 비교를 하기 때문
    	boolean isApply = novel.getRating().compareTo(BigDecimal.valueOf(4.0)) >= 0 && episodeCount >= 10 && novel.getLikeCount() >= 500;
    	
    	return isApply;
    }

	public Optional<NovelGradeRequest> getPremiumUserIdAndNovelId(Long currentUserId, Long novelId) {
		return novelGradeRequestRepo.findByUserIdAndNovelId(currentUserId,novelId);
	}

	
	// ===================================== 메인 소설 리스트 ======================================
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getRandemBestNovels(int count) {
		return novelRepo.findRandomBestNovels(count);
	}
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getNovelsByFreeGrade(int limit) {
		return novelRepo.findFreeNovels(limit);
	}
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getNovelsByPaidGrade(int limit) {
		return novelRepo.findPaidNovels(limit);
	}
	@Transactional(readOnly = true)
	public Map<String, List<NovelListItemDto>> getFixedGenreNovels(int limit) {
		List<String> genreNames = List.of("판타지", "로맨스", "무협", "로판", "현판", "드라마");
		Map<String, List<NovelListItemDto>> result = new LinkedHashMap<>();
		
		for(String genreName : genreNames) {
			List<NovelListItemDto> novels = novelRepo.findNovelsByGenreName(genreName, limit);
			result.put(genreName, novels);
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getEventNovels(int limit) {
		return novelRepo.findRandomNovels(limit);
	}
	
	
	// home - 베스트탭 소설 목록
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getRecommendedBest(int limit) {
		return novelRepo.findAllOrderByLikeDesc(limit);
	}
	
	// home - 오늘 신작 
	// 최근 날짜순으로 소설 목록
	@Transactional(readOnly = true)
	public Map<LocalDate, List<NovelListItemDto>> getRecommendedNew() {
		LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
		List<Novel> novels = novelRepo.findByCreatedTimeAfter(oneMonthAgo);
		
		// 날짜 내림차순
	return novels.stream()
	        .collect(Collectors.groupingBy(
	            novel -> novel.getCreatedTime().toLocalDate(),
	            () -> new TreeMap<>(Collections.reverseOrder()),
	            Collectors.mapping(novel -> NovelListItemDto.fromEntity(novel, "", 0L), Collectors.toList())
	        ));
	}

	
	// ===================================== 유료/무료 소설 리스트 ======================================
	// 추천 신작 무료 소설 (최근순)
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getRecommendNewNovels(int grade, int limit) {
		return novelRepo.findByGradeOrderByNew(grade, limit);
	}
	// 인기 연재 무료 소설 (좋아요/평점순)
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getPopularSerialNovels(int grade, int state, int limit) {
		return novelRepo.findByGradeAndSerialOrderByPopularity(grade, state, limit);
	}
	// 인기 완결 무료 소설
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getPopularCompletedNovels(int grade, int state, int limit) {
		return novelRepo.findByGradeAndCompletedOrderByPopularity(grade, state, limit);
	}
	
	// 장르별 소설 (장르 지정)
	@Transactional(readOnly = true)
	public Map<String, List<NovelListItemDto>> getGenreNovels(int grade, int limit) {
		List<String> genreNames = List.of("판타지", "로맨스", "무협", "로판", "현판", "드라마");
		Map<String, List<NovelListItemDto>> result = new LinkedHashMap<>();
		
		for(String genreName : genreNames) {
			List<NovelListItemDto> novels = novelRepo.findByGradeAndGenreRandom(grade, genreName, limit);
			result.put(genreName, novels);
		}
		return result;
	}
	// 이벤트 소설
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getEventNovels(int grade, int limit) {
		return novelRepo.findByGradeEventOrderByNew(grade, limit);
	}
	
	
	// 베스트 소설 목록
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getRecommendedFreePaidBest(int grade, int limit) {
		List<NovelListItemDto> novels = novelRepo.findOrderByLikeDesc(grade, limit);
		System.out.println(novels);
		return novels;
	}
	
	// 최근 날짜순으로 소설 목록
	@Transactional(readOnly = true)
	public Map<LocalDate, List<NovelListItemDto>> getRecommendedNew(int grade) {
		LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
		List<Novel> novels = novelRepo.findByGradeAndCreatedTimeAfter(grade, oneMonthAgo);
		
		// 날짜 내림차순으로 그룹화
		return novels.stream()
		        .collect(Collectors.groupingBy(
		            novel -> novel.getCreatedTime().toLocalDate(),
		            () -> new TreeMap<>(Collections.reverseOrder()),
		            Collectors.mapping(novel -> NovelListItemDto.fromEntity(novel, "", 0L), Collectors.toList())
		        ));
	}
	
	// 판타지 장르만
	@Transactional(readOnly = true)
	public List<NovelListItemDto> getNovelsByGenre(int grade, String genreName, int limit) {
		List<NovelListItemDto> novels = novelRepo.findByGradeAndGenre(grade, genreName, limit);
		log.info("판타지 소설 개수 : {}", novels);
		return novels;
	}
}
