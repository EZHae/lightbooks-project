package com.itwill.lightbooks.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelRating;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.repository.novel.NovelRepository;
import com.itwill.lightbooks.repository.rating.RatingRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NovelRatingService {

	private final RatingRepository ratingRepo;
	private final NovelRepository novelRepo;
	private final UserRepository userRepo;
	
	// 별점 평균 조회
	@Transactional(readOnly = true)
	public BigDecimal readRating(Long novelId) {
		BigDecimal averageRating = ratingRepo.findAverageRatingByNovelId(novelId);
		log.info("별점 평균 : {}", averageRating);
		return averageRating;
	}
	
	// 별점 총합 조회 - 테스트
	@Transactional(readOnly = true)
	public BigDecimal getTotalRatings(Long novelId) {
		BigDecimal total = ratingRepo.findTotalRaintgByNovelId(novelId);
		log.info("별점 총합 : {}", total);
		return total;
	}
	
	// 별점을 남긴 수 조회 - 테스트
	@Transactional(readOnly = true)
	public Long getCountRatings(Long novelId) {
		Long count = ratingRepo.countRatingsByNovelId(novelId);
		log.info("별점 남긴 수 : {}", count);
		return count != null ? count : 0L;
	}

	
	// 사용자 별점 입력 및 수정
	@Transactional
	public void saveOrUpdateRating(Long novelId, Long userId, BigDecimal rating) {
		Novel novel = novelRepo.findById(novelId).orElseThrow(() -> new IllegalArgumentException("소설을 찾을 수 없습니다"));
		User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
		
		// 기존의 별점을 확인
		NovelRating existRating = ratingRepo.findbyNovelIdAndUserId(novelId, userId);
		
		if(existRating != null) {
			existRating.setRating(rating); // 기존 별점 수정
			ratingRepo.save(existRating); // 변경된 별점 저장
			log.info("별점 수정됨: novelId={}, userId={}, rating={}", novelId, userId, rating);
		} else {
			NovelRating newRating = NovelRating.builder()
					.novel(novel)
					.user(user)
					.rating(rating)
					.build();
			ratingRepo.save(newRating);	
			log.info("별점 추가됨: novelId={}, userId={}, rating={}", novelId, userId, rating);
		}
	}
	
	// 현재 사용자가 해당 소설에 별점을 부여했는지 여부
	public Boolean hasUserRated(Long novelId, Long userId) {
		return ratingRepo.existsByNovelIdAndUserId(novelId, userId);
	}
	
	
	public BigDecimal findRatingByUserAndNovel(Long novelId, Long userId) {
		return ratingRepo.findRatingByUserAndNovel(novelId, userId).orElse(BigDecimal.ZERO);
	}
}
