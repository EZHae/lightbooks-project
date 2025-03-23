package com.itwill.lightbooks.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.Comment;
import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.CommentLikeDto;
import com.itwill.lightbooks.dto.CommentLikeRequestDto;
import com.itwill.lightbooks.dto.CommentUpdateDto;
import com.itwill.lightbooks.dto.EpisodeCommentRegisterDto;
import com.itwill.lightbooks.repository.comment.CommentRepository;
import com.itwill.lightbooks.service.EpisodeCommentService;
import com.itwill.lightbooks.service.EpisodeService;
import com.itwill.lightbooks.service.NovelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/novel/{novelId}/episode/{episodeId}")
@RequiredArgsConstructor
@Slf4j
public class EpisodeCommentController {

	private final NovelService novelService;
	private final EpisodeService episodeService;
	private final EpisodeCommentService episodeCommentService;
	private final CommentRepository commentRepo;
	
	// 해당 회차 댓글 페이지로 이동
	@GetMapping("comment")
	public String comment(@PathVariable(name = "novelId") Long novelId,@PathVariable(name = "episodeId") Long episodeId,
							Model model) {
		log.info("comment 페이지");
		
		
		Episode episode = episodeService.getEpisodeById(episodeId);
		Novel novel = novelService.searchById(novelId);
		model.addAttribute("novel", novel);
		model.addAttribute("episode", episode);
			
		return "episode/comment";
	}
	
	// 해당 회차의 댓글 리스트
	@GetMapping("comment/list")
	@ResponseBody
	public ResponseEntity<PagedModel<Comment>> comment(@PathVariable(name = "novelId") Long novelId,
													   @PathVariable(name = "episodeId") Long episodeId, 
													   @RequestParam(name = "p", defaultValue = "0") int pageNo) {
		log.info("comment page()");
		log.info("getCommentList(novelId={}, episodeId={}, pageNo={})", novelId,episodeId, pageNo);
		Episode episode = episodeService.getEpisodeById(episodeId);
		Novel novel = novelService.searchById(novelId);
		
		log.info("episode = {}", episode);
		log.info("novel = {}", novel);
		
		Page<Comment> page = episodeCommentService.readEpisode(episodeId, pageNo, Sort.by("modifiedTime").descending());
		log.info("페이지 수 = {}", page.getTotalPages());
		log.info("페이지 번호 = {}", page.getNumber());
		log.info("현재 페이지의 댓글 개수 = {}", page.getNumberOfElements());
		
       // 로그인 유저가 좋아요 누른 댓글 ID 목록
		
		return ResponseEntity.ok(new PagedModel<>(page));
	}

	
	// 댓글 작성
	@PostMapping("comment")
	@ResponseBody
	public ResponseEntity<Comment> registerComment(@RequestBody EpisodeCommentRegisterDto dto) {
		log.info("registerComment()");
		Comment comment = episodeCommentService.create(dto);
		
		return ResponseEntity.ok(comment);
	}
	
	// 댓글 삭제
	@DeleteMapping("comment/{commentId}")
	@ResponseBody
	public ResponseEntity<Long> deleteComment(@PathVariable Long commentId) {
		episodeCommentService.deleteComment(commentId);
		return ResponseEntity.ok(commentId);	
	}
	
	// 댓글 수정
	@PutMapping("comment/{commentId}")
	@ResponseBody
	public ResponseEntity<Long> updateComment(@PathVariable Long commentId,@RequestBody CommentUpdateDto dto) {
		log.info("updateComment id: {}", commentId);
		
		dto.setId(commentId);	
		episodeCommentService.update(dto);
		
		return ResponseEntity.ok(commentId);
	}
	
	// 댓글 좋아요/취소
	@PostMapping("comment/{commentId}/like")
	@ResponseBody
	public ResponseEntity<CommentLikeDto> toggleLike(@PathVariable Long commentId, @RequestBody CommentLikeRequestDto dto) {
		
		boolean liked = episodeCommentService.toggleLike(dto.getUserId(), commentId);
		int likeCount = episodeCommentService.getLikeCount(commentId);
		Comment comment = commentRepo.findById(commentId).orElseThrow();
		
		CommentLikeDto response = new CommentLikeDto(
				comment.getId(), comment.getEpisode().getId(), likeCount, liked);

		return ResponseEntity.ok(response);
	}
	
	// 댓글 스포일러
	@PutMapping("comment/{commentId}/spoiler")
	@ResponseBody
	public ResponseEntity<Long> spoiler(@PathVariable Long commentId,@RequestParam Long userId,@RequestParam int spoiler) {
		episodeCommentService.setSpoiler(userId, commentId, spoiler);
		
		return ResponseEntity.ok().build();
	}
}
