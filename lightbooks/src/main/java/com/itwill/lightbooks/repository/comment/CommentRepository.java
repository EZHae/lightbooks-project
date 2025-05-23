package com.itwill.lightbooks.repository.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Comment;
import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;

public interface CommentRepository extends JpaRepository<Comment, Long>{

	// 소설에 댓글 목록 페이징
	Page<Comment> findByNovelAndEpisodeEpisodeNumNotNull(Novel novel, Pageable pageable);
	// 회차에 댓글 목록 페이징
	Page<Comment> findByEpisode(Episode episode, Pageable pageable);
	
	Comment findByIdAndEpisodeId(Long id, Long episodeId);
	
	List<Comment> findByEpisodeId(Long episodeId);
	
//	Comment findByIdAndUserIdAndSpoiler(Long id, Long userId, int spoiler);
}
