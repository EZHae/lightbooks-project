package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelRating;

public interface NovelRepository extends JpaRepository<Novel, Long>, NovelQuerydsl {
	
	// 좋아요 증감
	@Modifying
	@Transactional
	@Query("UPDATE Novel n SET n.likeCount = n.likeCount +1 WHERE n.id = :novelId")
	void increaseLikeCount(@Param("novelId") Long novelId);

	// 좋아요 감소
	@Modifying
	@Transactional
	@Query("UPDATE Novel n SET n.likeCount = n.likeCount -1 WHERE n.id = :novelId AND n.likeCount > 0")
	void decreaseLikeCount(@Param("novelId") Long novelId);

}
