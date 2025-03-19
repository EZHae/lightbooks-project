package com.itwill.lightbooks.repository.rating;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.lightbooks.domain.NovelRating;

public interface RatingRepository extends JpaRepository<NovelRating, Long> {
	
	// 평균 계산 ( 한 소설에 평점 매긴 수 / 총합 ) SUM / COUNT
	@Query("SELECT COALESCE(SUM(nr.rating) / COUNT(nr.rating), 0) FROM NovelRating nr WHERE nr.novel.id = :novelId")
	BigDecimal findAverageRatingByNovelId(@Param("novelId") Long novelId);

	// 평점 리뷰 수 계산
	@Query("SELECT COUNT(nr) FROM NovelRating nr WHERE nr.novel.id = :novelId")
	Long countRatingsByNovelId(@Param("novelId") Long novelId);
	
	// 평점 총합 계산
	@Query("SELECT COALESCE(SUM(nr.rating),0) FROM NovelRating nr WHERE nr.novel.id = :novelId")
	BigDecimal findTotalRaintgByNovelId(@Param("novelId") Long novelId);

	// 평점 테이블에서 유저아이디와 소설아이디를 조회
	@Query("SELECT nr FROM NovelRating nr WHERE nr.novel.id = :novelId AND nr.user.id = :userId")
	NovelRating findbyNovelIdAndUserId(@Param("novelId") Long novelId, @Param("userId") Long userId);

	Boolean existsByNovelIdAndUserId(Long novelId, Long userId);

	@Query("SELECT n.rating FROM NovelRating n WHERE n.novel.id = :novelId AND n.user.id = :userId")
	Optional<BigDecimal> findRatingByUserAndNovel(@Param("novelId")Long novelId, @Param("userId")Long userId);
}
