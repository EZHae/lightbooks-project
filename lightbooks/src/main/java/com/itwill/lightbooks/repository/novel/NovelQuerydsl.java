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
	List<Novel> findFreeNovels(int limit);
	List<Novel> findPaidNovels(int limit);
	List<Novel> findNovelsByGenreName(String genreName, int limit);
	List<Novel> findRandomNovels(int limit);
	
	// 무료 페이지
	List<NovelListItemDto> findByFreeGradeOrderByNew(int limit);				
	List<NovelListItemDto> findByFreeGradeAndSerialOrderByPopularity(int limit);
	List<NovelListItemDto> findByFreeGradeAndCompletedOrderByPopularity(int limit);
	List<NovelListItemDto> findByFreeGradeEventOrderByNew(int limit);
	List<NovelListItemDto> findByFreeGradeAndGenreRandom(String genreName, int limit);
	List<Novel> findFreeOrderByLikeDesc();
	List<NovelListItemDto> findbyGradeAndGenre(int grade, String genreName, int limit);
	
}
