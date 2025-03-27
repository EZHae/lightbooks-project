package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.NovelListItemDto;
import com.itwill.lightbooks.dto.NovelSearchDto;

public interface NovelQuerydsl {
	
	Novel searchById(Long id);
	
	Novel searchByIdWithGenre(Long id);

	List<Novel> searchByUserIdWithGenre(Long userId);
	
	void updateNovel(Long novelId, String title, String intro, String coverSrc, Integer ageLimit, Integer state, List<String> days, Integer genreId);
	
	Page<Novel> searchByKeyword(NovelSearchDto dto, Pageable pageable);
	
	// 메인 페이지
	List<NovelListItemDto> findRandomBestNovels(int count);
	List<NovelListItemDto> findFreeNovels(int limit);
	List<NovelListItemDto> findPaidNovels(int limit);
	List<NovelListItemDto> findNovelsByGenreName(String genreName, int limit);
	List<NovelListItemDto> findRandomNovels(int limit);
	List<NovelListItemDto> findAllOrderByLikeDesc(int limit);
	
	// 무료 페이지 / 유료 페이지 
	List<NovelListItemDto> findByGradeOrderByNew(int grade ,int limit);				
	List<NovelListItemDto> findByGradeAndSerialOrderByPopularity(int grade,int state ,int limit);
	List<NovelListItemDto> findByGradeAndCompletedOrderByPopularity(int grade,int state ,int limit);
	List<NovelListItemDto> findByGradeEventOrderByNew(int grade, int limit);
	List<NovelListItemDto> findByGradeAndGenreRandom(int grade, String genreName, int limit);
	List<NovelListItemDto> findOrderByLikeDesc(int grade, int limit);
	List<NovelListItemDto> findByGradeAndGenre(int grade, String genreName, int limit);
	
}
