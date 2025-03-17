package com.itwill.lightbooks.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelLike;
import com.itwill.lightbooks.domain.NovelLikeId;
import com.itwill.lightbooks.domain.User;

public interface LikeRepository extends JpaRepository<NovelLike, NovelLikeId>{
	
	boolean existsByUserAndNovel(User user, Novel novel); // 사용자가 특정 소설을 좋아요했는지 확인
	
	void deleteByUserAndNovel(User user, Novel novel); //좋아요 취소
	
	int countByNovel(Novel novel); //해당 소설의 좋아요 개수 반환
}
