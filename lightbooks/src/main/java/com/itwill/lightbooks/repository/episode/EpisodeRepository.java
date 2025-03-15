package com.itwill.lightbooks.repository.episode;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;

public interface EpisodeRepository extends JpaRepository<Episode, Long>{
	
	//Episode를 ID를 사용하여 조회할 때, 연관된 Novel 정보 (소설 제목 등)도 함께 가져오기 위한 메서드
	@Query("SELECT e FROM Episode e JOIN FETCH e.novel WHERE e.id = :episodeId")
	Optional<Episode> findByIdWithNovel(@Param("episodeId") Long episodeId);
	
	//특정 Novel에 속하는 모든 Episode 목록을 회차 번호(episodeNum)순으로 정렬하여 가져오기 위한 메서드
	//소설 상세 보기 페이지에서 회차 목록을 표시할 때 사용
	List<Episode> findByNovelOrderByEpisodeNum(Novel novel);
	
	//회차 상세 보기 페이지에서 "다음 화" 버튼을 구현하는 데 사용
	Optional<Episode> findFirstByNovelIdAndEpisodeNumGreaterThanOrderByEpisodeNumAsc(Long novelId, Integer episodeNum);
	
	//회차 상세 보기 페이지에서 "이전 화" 버튼을 구현하는 데 사용
	Optional<Episode> findFirstByNovelIdAndEpisodeNumLessThanOrderByEpisodeNumDesc(Long novelId, Integer episodeNum);
	
	//Novel에서 등록된 최대 회차넘버를 셀렉
	@Query("select max(e.episodeNum) from Episode e where e.novel.id = :novelId")
    Integer findMaxEpisodeNumByNovelId(@Param("novelId") Long novelId);
}