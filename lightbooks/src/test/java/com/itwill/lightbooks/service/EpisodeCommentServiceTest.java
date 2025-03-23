package com.itwill.lightbooks.service;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Comment;
import com.itwill.lightbooks.dto.CommentLikeDto;
import com.itwill.lightbooks.dto.CommentUpdateDto;
import com.itwill.lightbooks.dto.EpisodeCommentRegisterDto;
import com.itwill.lightbooks.service.EpisodeCommentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
@Rollback(false)
public class EpisodeCommentServiceTest {
//	
//	@Autowired
//	private EpisodeCommentService episodeCommentService;
//	
//	@Test
//	public void testCreate() {
//		EpisodeCommentRegisterDto dto = new EpisodeCommentRegisterDto(6L, 62L, 35L, "댓글 8번", 0, "최호철", 0);
//		Comment comment = episodeCommentService.create(dto);
//	}
	
//	@Test
//	public void testDelete() {
//		episodeCommentService.deleteComment(8L);
//	}
	
//	@Test
//	public void dummyData() {
//		for(int i=1; i <= 10; i++) {
//			EpisodeCommentRegisterDto dto = new EpisodeCommentRegisterDto(6L, 62L, 35L, "댓글 "+ i + "번", 0, "최호철", 0);
//			Comment comment = episodeCommentService.create(dto);
//			assertThat(comment).isNotNull();
//		}
//	}
	
//	@Test
//	public void testUpdate() {
//		CommentUpdateDto dto = new CommentUpdateDto();
//		dto.setId(6l);
//		dto.setSpoiler(0);
//		dto.setText("텟트");
//		episodeCommentService.update(dto);	
//	}
	
//	@Test
//	public void testLike() {
//		episodeCommentService.like(6l, 21l);
//	}	
	
//	@Test
//	public void testUnLike() {
//		episodeCommentService.unlike(6l, 21l);
//	}
	
}
