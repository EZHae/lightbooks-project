package com.itwill.lightbooks.web;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.lightbooks.dto.NovelRatingResponse;
import com.itwill.lightbooks.service.NovelRatingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NovelRatingController {
	
	private final NovelRatingService novelRatingService;
	
	// 별점 평균 조회
	@GetMapping("/{novelId}/rating")
	public ResponseEntity<NovelRatingResponse> ratingAvg(@PathVariable Long novelId) {
		BigDecimal avgRating  = novelRatingService.readRating(novelId);
		
		// 응답 호출
		NovelRatingResponse response = new NovelRatingResponse(avgRating);
		System.out.println("조회된 평균 별점: " + avgRating);
		
		return ResponseEntity.ok(response);
	}
	
	// 별점 추가/수정 
	@PostMapping("/{novelId}/rating")
	public ResponseEntity<?> insertRating(@PathVariable Long novelId,
											@RequestBody Map<String, Object> payload) {
		
		Long userId = Long.parseLong(payload.get("userId").toString());
		BigDecimal rating = new BigDecimal(payload.get("rating").toString());
		
		novelRatingService.saveOrUpdateRating(novelId,userId,rating);
		
		if (userId == null || userId == 0) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }
		
		return ResponseEntity.ok(Map.of("message", "별점이 저장되었습니다.", "status", "success"));
	}
	
	// 별점을 부여했는가?
	@GetMapping("/{novelId}/user/{userId}/rating")
	public ResponseEntity<Boolean> checkUserRating(@PathVariable Long novelId, @PathVariable Long userId) {
	    boolean hasRated = novelRatingService.hasUserRated(novelId, userId);
	    return ResponseEntity.ok(hasRated);
	}
}
