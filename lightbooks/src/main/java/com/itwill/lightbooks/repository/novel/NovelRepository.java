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
	
	List<Novel> findByUserId(Long userId);
	
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

	// 마일리지 교환샵에서 사용할 메서드, 유료 소설만 키워드로 검색하기
	@Query("SELECT n FROM Novel n WHERE n.grade = 1 AND LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Novel> searchPaidAndKeyword(@Param("keyword") String keyword);
}
