package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelRating;

public interface NovelRepository extends JpaRepository<Novel, Long>, NovelQuerydsl {
	
	List<Novel> findByUserId(Long userId);
	
	@Query("UPDATE NOVEL n SET n.likeCount = n.likeCount +1 WHERE n.id = :novelId")
	void increaseLikeCount(Long novelId);

	// 좋아요 감소
	@Query("UPDATE NOVEL n SET n.likeCount = n.likeCount -1 WHERE n.id = :novelId AND n.likeCount > 0")
	void decreaseLikeCount(Long novelId);

}
