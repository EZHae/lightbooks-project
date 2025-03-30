package com.itwill.lightbooks.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Comment;
import com.itwill.lightbooks.domain.CommentLike;
import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.CommentUpdateDto;
import com.itwill.lightbooks.dto.EpisodeCommentRegisterDto;
import com.itwill.lightbooks.dto.NovelCommentResponseDto;
import com.itwill.lightbooks.repository.comment.CommentLikeRepository;
import com.itwill.lightbooks.repository.comment.CommentRepository;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
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
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long currentUserId = ((User) auth.getPrincipal()).getUserId();
       
		// 현재 유저가 좋아요한 댓글 ID 리스트
		List<Long> likedIds = commentLikeRepo.findLikedCommentIdsByUser(currentUserId);

		// 각 댓글에 likedByUser 설정
		page.getContent().forEach(comment -> {
		    if (likedIds.contains(comment.getId())) {
		        comment.setLikedByUser(true);
		    }
		});
		
		return page;
	}
	
	// 소설 상세보기 댓글 목록
	@Transactional(readOnly = true)
	public Page<NovelCommentResponseDto> readNovel(Long novelId, int pageNo, Sort sort,Long currentUserId) {
		log.info("read (episode Id : {}, pageNo : {}, sort : {}", novelId, pageNo, sort);
		Pageable pageable = PageRequest.of(pageNo, 10, sort);
		
		Novel novel = novelRepo.findById(novelId).orElseThrow(() -> new EntityNotFoundException("해당 소설이 없습니다."));
		log.info("read (episode Id: {}", novelId);
		
		Page<Comment> page= commentRepo.findByNovel(novel, pageable);
		
		// 현재 유저가 좋아요한 댓글 ID 리스트
		List<Long> likedIds = commentLikeRepo.findLikedCommentIdsByUser(currentUserId);

		return page.map(comment -> {
			boolean liked = likedIds.contains(comment.getId());
			
			Episode episode = comment.getEpisode();
			
			return NovelCommentResponseDto.builder()
					.commentId(comment.getId())
					.nickname(comment.getNickname())
					.text(comment.getText())
					.spoiler(comment.getSpoiler())
					.likeCount(comment.getLikeCount())
					.likedByUser(liked)
					.episodeTitle(episode.getTitle())
					.episodeNum(episode.getEpisodeNum())
					.modifiedTime(comment.getModifiedTime())
					.userId(comment.getUser().getUserId())
					.novelId(novelId)
					.episodeId(episode.getId())
		            .build();
		});
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
	public boolean toggleLike(Long userId, Long commentId) {
		
		Comment comment = commentRepo.findById(commentId).orElseThrow();
		User user = userRepo.findById(userId).orElseThrow();
		
		CommentLike existing = commentLikeRepo.findByUserAndComment(user, comment);
		
		if(existing != null) {
			commentLikeRepo.delete(existing); // 좋아요 취소
			comment.setLikeState(false); // 좋아요 감소
			return false;
		} else {
			CommentLike like = new CommentLike();
			like.toEntity(user, comment);
			commentLikeRepo.save(like); // 좋아요 추가
			comment.setLikeState(true); // 좋아요 증가
			return true;
		}
	}
	// 좋아요 카운트
	@Transactional
	public int getLikeCount(Long commentId) {
		return commentLikeRepo.countByCommentId(commentId);
	}
	
	// 해당 유저가 좋아요 했는지
	@Transactional
	public boolean isLikeByUser(Long userId, Long commentId) {
		return commentLikeRepo.existsByUserIdAndCommentId(userId, commentId);
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
	

	public List<Long> findLikedCommentIdsByUser(Long currentUserId) {
		commentLikeRepo.findLikedCommentIdsByUser(currentUserId);
		
		return null;
	}

	public List<Long> findLikedCommentIds(Long currentUserId, List<Long> commentIds) {
	    if (commentIds == null || commentIds.isEmpty()) {
	        return Collections.emptyList();
	    }
	    return commentLikeRepo.findLikedCommentIdsByUserAndCommentIds(currentUserId, commentIds);
	}
}
