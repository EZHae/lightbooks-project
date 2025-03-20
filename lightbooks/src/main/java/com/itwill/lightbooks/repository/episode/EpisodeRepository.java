package com.itwill.lightbooks.repository.episode;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.EpisodeListDto;

public interface EpisodeRepository extends JpaRepository<Episode, Long>{
	
	//Episode를 ID를 사용하여 조회할 때, 연관된 Novel 정보 (소설 제목 등)도 함께 가져오기 위한 메서드
	//에피소드 상세보기 연관
	@Query("SELECT e FROM Episode e JOIN FETCH e.novel WHERE e.id = :episodeId")
	Optional<Episode> findByIdWithNovel(@Param("episodeId") Long episodeId);
	
	//회차 상세 보기 페이지에서 "다음 화" 버튼을 구현하는 데 사용
	Optional<Episode> findFirstByNovelIdAndEpisodeNumGreaterThanOrderByEpisodeNumAsc(Long novelId, Integer episodeNum);
	
	//회차 상세 보기 페이지에서 "이전 화" 버튼을 구현하는 데 사용
	Optional<Episode> findFirstByNovelIdAndEpisodeNumLessThanOrderByEpisodeNumDesc(Long novelId, Integer episodeNum);
	
	//Novel에서 등록된 최대 회차넘버를 셀렉
	@Query("select max(e.episodeNum) from Episode e where e.novel.id = :novelId")
    Integer findMaxEpisodeNumByNovelId(@Param("novelId") Long novelId);
	
	//소설 상세 페이지의 에피소드 기본 목록 (공지 제외, 회차 번호 오름차순, 페이징)
	Page<Episode> findByNovelAndCategoryNotOrderByEpisodeNumAsc(Novel novel, Integer category, Pageable pageable);
	
    //공지(category=0)를 제외한 나머지 에피소드를 회차 번호 내림차순(최신순)
	Page<Episode> findByNovelAndCategoryNotOrderByEpisodeNumDesc(Novel novel, Integer category, Pageable pageable);
	
	//특정 소설의 공지(category=0)를 최신순으로 가져올 때 사용.
	Page<Episode> findByNovelAndCategoryOrderByCreatedTimeDesc(Novel novel, Integer category, Pageable pageable);
	
	//특정 소설의 공지(category=0)를 오름차순으로 가져올 때 사용.
	Page<Episode> findByNovelAndCategoryOrderByCreatedTimeAsc(Novel novel, Integer category, Pageable pageable);
	
	
	Page<Episode> findByNovelAndCategoryOrderByEpisodeNumDesc(Novel novel, Integer category, Pageable pageable);
	
	
	Page<Episode> findByNovelAndCategoryOrderByEpisodeNumAsc(Novel novel, Integer category, Pageable pageable);
	
	//소설 상세 페이지에서 "첫 화 보기" 버튼에 사용할 첫 번째 에피소드의 ID를 찾기
	Optional<Episode> findFirstByNovelOrderByEpisodeNumAsc(Novel novel);

	Page<EpisodeListDto> getEpisodesByNovelAndCategory(Long novelId, Integer category, String sortOrder, Pageable pageable);
	
	boolean existsByNovelIdAndEpisodeNum(Long novelId, int episodeNum);

	// 해당 소설의 회차 개수를 불러올 때 사용
	@Query("SELECT COUNT(e) FROM Episode e WHERE e.novel.id = :novelId") 
	Long countNovelId(Long novelId);
	
	
	
	
	
	
	 //-- 선택 사항 (필요에 따라 추가) ---
    //(4) 특정 소설의 모든 에피소드를 회차 번호 *내림차순*, 페이징
   //Page<Episode> findByNovelOrderByEpisodeNumDesc(Novel novel, Pageable pageable);
    //(5) 특정 소설, 특정 카테고리의 에피소드를 회차 번호 *내림차순*, 페이징
   //Page<Episode> findByNovelAndCategoryOrderByEpisodeNumDesc(Novel novel, Integer category, Pageable pageable);
    //(6) 특정 소설에서 특정 카테고리가 *아닌* 에피소드들을 회차 번호 *내림차순*, 페이징
   //Page<Episode> findByNovelAndCategoryNotOrderByEpisodeNumDesc(Novel novel, Integer category, Pageable pageable);
     //(7) 특정 소설의 모든 에피소드 생성일 내림차순, 페이징
    //Page<Episode> findByNovelOrderByCreatedTimeDesc(Novel novel, Pageable pageable);
    //(9) 특정 소설, 특정 카테고리 아닌 에피소드 생성일 내림차순, 페이징
    //Page<Episode> findByNovelAndCategoryNotOrderByCreatedTimeDesc(Novel novel, Integer category, Pageable pageable);
	
	
	
}