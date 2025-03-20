package com.itwill.lightbooks.repository.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itwill.lightbooks.domain.Bookmark;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.User;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	
	//유료회차 구매 여부 확인
	boolean existsByUserIdAndNovelIdAndEpisodeIdAndType(Long userId, Long novelId, Long episodeId, Integer type);

	// 유저아이디와 소설아이디로 좋아요 (추가/삭제) 상태를 확인하는데 사용 
	boolean existsByUserIdAndNovelIdAndType(Long userId, Long novelId, Integer type);

	// 유저아이디와 소설아이디로 좋아요 삭제하는데 사용
	void deleteByUserIdAndNovelIdAndType(Long userId, Long novelId, Integer type);

	// 해당 소설의 좋아요 개수 조회
	@Query ("SELECT COUNT(b) FROM Bookmark b WHERE b.novel.id = :novelId AND b.type = :type")
	int countByNovelIdAndType(Long novelId, Integer type);
}
