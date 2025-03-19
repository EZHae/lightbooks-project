package com.itwill.lightbooks.repository;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.repository.rating.RatingRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@Transactional
public class RatingRepositoryTest {
	
	@Autowired
	private RatingRepository ratingRepo;
	
//	@Test
	public void RatingAvgTest() {
		Long novelId = 17L;
		BigDecimal avg = ratingRepo.findAverageRatingByNovelId(novelId);
		log.info("평균 조회 (novelId: {}): {}", novelId, avg);
	}
	
//	@Test
	public void RatingSumTest() {
		Long novelId = 17L;
		BigDecimal total = ratingRepo.findTotalRaintgByNovelId(novelId);
		log.info("총합 조회(novelId: {}): {}", novelId, total);
	}
	
//	@Test
	public void RatingCountTest() {
		Long novelId = 17L;
		Long count = ratingRepo.countRatingsByNovelId(novelId);
		log.info("소설 개수 조회(novelId: {}): {}", novelId, count);
	}
}
