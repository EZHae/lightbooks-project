package com.itwill.lightbooks.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelLike;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.repository.like.LikeRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
	
	private final LikeRepository likeRepo;
	private final UserRepository userRepo;
	private final NovelRepository novelRepo;
	
	
	// 좋아요 버튼 선택 및 해제 서비스
	@Transactional
	public boolean toggleLike(Long userId, Long novelId) {
		
		User user = userRepo.findById(userId).orElseThrow();
		Novel novel = novelRepo.findById(novelId).orElseThrow();
		
		if(likeRepo.existsByUserAndNovel(user, novel)) {
			likeRepo.deleteByUserAndNovel(user, novel);
			novel.setLikeCount(novel.getLikeCount() - 1); //좋아요 개수 감소
			return false; // 좋아요 취소
		} else {
			likeRepo.save(new NovelLike(user, novel));
			novel.setLikeCount(novel.getLikeCount() + 1); // 좋아요 개수 증가
			return true;
		}
	}

	// 좋아요 카운트 개수 조회
	public int getLikeCount(Long novelId) {
		Novel novel = novelRepo.findById(novelId).orElseThrow();
		return likeRepo.countByNovel(novel);
	}
	
	// 
	public boolean existsByUserAndNovel(Long userId, Long novelId) {
		User user = userRepo.findById(userId).orElseThrow();
		Novel novel = novelRepo.findById(novelId).orElseThrow();
		
		return likeRepo.existsByUserAndNovel(user, novel);
	}
}
