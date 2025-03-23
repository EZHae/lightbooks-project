package com.itwill.lightbooks.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Comment;
import com.itwill.lightbooks.domain.CommentLike;
import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.CommentUpdateDto;
import com.itwill.lightbooks.dto.EpisodeCommentRegisterDto;
import com.itwill.lightbooks.repository.comment.CommentLikeRepository;
import com.itwill.lightbooks.repository.comment.CommentRepository;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EpisodeCommentService {
	
	private final CommentRepository commentRepo;
	private final CommentLikeRepository commentLikeRepo;
	private final NovelRepository novelRepo;
	private final EpisodeRepository episodeRepo;
	private final UserRepository userRepo;
	
	
	// 회차보기에 등록할 댓글 서비스
	@Transactional
	public Comment create(EpisodeCommentRegisterDto dto) {
		log.info("comment service()");
		
		Episode episode = episodeRepo.findById(dto.getEpisodeId()).orElseThrow();
		log.info("댓글 등록할 소설 아이디 : {}", episode);
		User user = userRepo.findById(dto.getUserId()).orElseThrow();
		Novel novel = novelRepo.findById(dto.getNovelId()).orElseThrow();
		log.info("novel id : {}", novel);
		log.info("user id : {}", user);
		Comment entity = Comment.builder()	
				.episode(episode)
				.user(user)
				.novel(novel)
				.nickname(dto.getNickname())
				.spoiler(dto.getSpoiler())
				.text(dto.getText())
				.likeCount(0)
				.build();
		
		entity = commentRepo.save(entity);
		
		return entity;
	}
	
	// 회차보기 댓글 목록
	@Transactional(readOnly = true)
	public Page<Comment> readEpisode(Long episodeId, int pageNo, Sort sort) {
		log.info("read (episode Id : {}, pageNo : {}, sort : {}", episodeId, pageNo, sort);
		Pageable pageable = PageRequest.of(pageNo, 10, sort);
		
		Episode episode = episodeRepo.findById(episodeId).orElseThrow(() -> null);
		log.info("read (episode Id: {}", episodeId);
		
		Page<Comment> page= commentRepo.findByEpisode(episode, pageable);
		
		return page;
	}
	
	// 소설 상세보기 댓글 목록
	@Transactional(readOnly = true)
	public Page<Comment> readNovel(Long novelId, int pageNo, Sort sort) {
		log.info("read (episode Id : {}, pageNo : {}, sort : {}", novelId, pageNo, sort);
		Pageable pageable = PageRequest.of(pageNo, 10, sort);
		
		Novel novel = novelRepo.findById(novelId).orElseThrow(() -> null);
		log.info("read (episode Id: {}", novelId);
		
		Page<Comment> page= commentRepo.findByNovel(novel, pageable);
		
		return page;
	}
	
	// 댓글 삭제
	@Transactional
	public Long deleteComment(Long commentId) {
		commentRepo.deleteById(commentId);
		return commentId;
	}
	
	// 댓글 업데이트
	@Transactional
	public void update(CommentUpdateDto dto) {
		log.info("comment update()");
		Comment entity = commentRepo.findById(dto.getId()).orElseThrow();
		log.info("업데이트 댓글 : {}", entity);
		
		entity.update(dto.getText() , dto.getSpoiler()); 
	}
	
	// 댓글 좋아요 - 해당 댓글을 찾아서 해당 댓글을 업데이트?
	// 좋아요
	@Transactional
	public void like(Long userId, Long commentId) {
		if(commentLikeRepo.existsByUserIdAndCommentId(commentId, userId)) {
			throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
		}
		
		Comment entity = commentRepo.findById(commentId).orElseThrow();
		entity.setLikeState(true);
		
		User user = userRepo.findById(userId).orElseThrow();
		
		CommentLike like = new CommentLike();
		like.toEntity(user, entity);
		
		commentLikeRepo.save(like);
	}
	// 좋아요 취소
	@Transactional
	public void unlike(Long userId, Long commentId) {
		CommentLike like = commentLikeRepo.findByUserIdAndCommentId(userId, commentId);
		commentLikeRepo.delete(like);
		
		Comment entity = commentRepo.findById(commentId).orElseThrow();
		entity.setLikeState(false);
	}
	
	// 스포일러 여부
	@Transactional
	public void setSpoiler(Long userId, Long commentId, int isSpoiler) {
		Comment comment = commentRepo.findById(commentId).orElseThrow();
		
		if(!comment.getUser().getId().equals(userId)) {
			throw new IllegalArgumentException("댓글 작성자만 스포일러 여부를 수정할 수 있습니다.");
		}
		comment.setSpoiler(isSpoiler);
	}
	
}
