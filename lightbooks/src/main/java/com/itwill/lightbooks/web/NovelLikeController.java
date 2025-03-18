package com.itwill.lightbooks.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.itwill.lightbooks.dto.LikeRequestDto;
import com.itwill.lightbooks.service.NovelLikeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class NovelLikeController {
	
	private final NovelLikeService novelLikeService;
	
	@PostMapping("/like")
	public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody LikeRequestDto dto) {
		
		Long userId = dto.getUserId();
		Long novelId = dto.getNovelId();
		
		if(userId == 0 || userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
		}
		
		log.info("현재 좋아요 user Id: {}", userId);
        log.info("현재 좋아요 novel Id: {}", novelId);
		
		boolean liked = novelLikeService.toggleLike(userId, novelId);
		int likeCount = novelLikeService.getLikeCount(novelId);
		
		return ResponseEntity.ok(Map.of("liked", liked, "likeCount", likeCount));
	}
	
	// 페이지가 로드될 때 좋아요 개수 갖고오는 메서드
	@GetMapping("/like/count/{novelId}")
	public ResponseEntity<Map<String, Object>> getLikeCount(@PathVariable Long novelId, @RequestParam Long userId) {
		
		boolean liked = novelLikeService.existsByUserAndNovel(userId, novelId);
		Integer likeCount = novelLikeService.getLikeCount(novelId);
		return ResponseEntity.ok(Map.of("liked", liked,"likeCount",likeCount));
	}
}
