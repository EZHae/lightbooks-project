package com.itwill.lightbooks.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.Post;
import com.itwill.lightbooks.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
	
	private final PostService postService;

	@GetMapping("/notice")
	public void readNotices() {
		log.info("readNotices");
	}
	
	@GetMapping("/notice/details")
	public String detailsNotice(Model model, @RequestParam(name = "id") Long id) {
		log.info("detailsNotice(id={})");
		
		Post post = postService.read(id);
		
		model.addAttribute("post", post);
		
		return "post/details";
	}
	
	@ResponseBody
	@GetMapping("/notice/read")
	public ResponseEntity<List<Post>> readNotice() {
		List<Post> posts = postService.readAll();
		
		return ResponseEntity.ok(posts);
	}
	
	@ResponseBody
	@GetMapping("/notice/read/highlight")
	public ResponseEntity<List<Post>> readNoticeHighlight() {
		List<Post> posts = postService.readOnlyHighlight();
		
		return ResponseEntity.ok(posts);
	}
}
