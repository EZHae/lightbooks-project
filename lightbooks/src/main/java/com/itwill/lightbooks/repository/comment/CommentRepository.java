package com.itwill.lightbooks.repository.comment;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Comment;
import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;

public interface CommentRepository extends JpaRepository<Comment, Long>{

	// 소설에 댓글 목록 페이징
	Page<Comment> findByNovel(Novel novel, Pageable pageable);
	// 회차에 댓글 목록 페이징
	Page<Comment> findByEpisode(Episode episode, Pageable pageable);
	
	Comment findByIdAndEpisodeId(Long id, Long episodeId);
	
//	Comment findByIdAndUserIdAndSpoiler(Long id, Long userId, int spoiler);
}
