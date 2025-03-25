package com.itwill.lightbooks.repository.bookmark;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
	
	// 추가) 북마크 업데이트 및 북마크 존재 여부 확인
	Bookmark findByUserIdAndEpisodeId(Long userId, Long episodeId);
	
	// 좋아요 누른 소설 페이징 조회
	@Query("SELECT b FROM Bookmark b JOIN FETCH b.novel WHERE b.user.id = :userId AND b.type = 0 ORDER BY b.novel.likeCount DESC")
	Page<Bookmark> findLikedNovelsByUserIdOrderByLikeCountDesc(Long userId, Pageable pageable);

    // 최근 본 회차 페이징 조회
    @Query("SELECT b FROM Bookmark b JOIN b.episode e WHERE b.user.id = :userId AND b.type = 1 AND e.category != 0 ORDER BY b.accessTime DESC")
    Page<Bookmark> findRecentlyWatchedEpisodesByUserIdExcludeNotice(@Param("userId") Long userId, Pageable pageable);

    // 구매한 소설 페이징 조회
    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId AND b.type = 2 ORDER BY b.createdTime DESC")
    Page<Bookmark> findPurchasedNovelsByUserIdOrderByCreatedTimeDesc(Long userId, Pageable pageable);
    
}
