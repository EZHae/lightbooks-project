package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.NovelSearchDto;

public interface NovelQuerydsl {
	
	Novel searchById(Long id);
	
	Novel searchByIdWithGenre(Long id);

	List<Novel> searchByUserIdWithGenre(Long userId);
	
	void updateNovel(Long novelId, String title, String intro, String coverSrc, Integer ageLimit, Integer state, List<String> days, Integer genreId);
	
	Page<Novel> searchByKeyword(NovelSearchDto dto, Pageable pageable);
	
	List<Novel> findRandomBestNovels(int count);
	List<Novel> findFreeNovels(int limit);
	List<Novel> findPaidNovels(int limit);
	List<Novel> findNovelsByGenreName(String genreName, int limit);
	List<Novel> findRandomNovels(int limit);
	
}
