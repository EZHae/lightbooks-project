package com.itwill.lightbooks.repository.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	
	//유료회차 구매 여부 확인
	boolean existsByUserIdAndNovelIdAndEpisodeIdAndType(Long userId, Long novelId, Long episodeId, Integer type);
}
