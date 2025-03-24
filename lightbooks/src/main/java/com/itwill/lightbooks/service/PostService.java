package com.itwill.lightbooks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itwill.lightbooks.domain.Post;
import com.itwill.lightbooks.dto.PostCreateDto;
import com.itwill.lightbooks.dto.PostUpdateDto;
import com.itwill.lightbooks.dto.PostUpdateHighlightDto;
import com.itwill.lightbooks.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepo;
	
	public List<Post> readAll() {
		List<Post> posts = postRepo.findAll();
		
		return posts;
	}
	
	public void savePost(PostCreateDto dto) {
		Post post = dto.toEntity();
		
		Post savedPost = postRepo.save(post);
		log.info("savedPost={}", savedPost);
	}
	
	public void updatePostHighlight(PostUpdateHighlightDto dto) {
		Post post = postRepo.findById(dto.getId()).orElseThrow();
		
		post.updateHighlight(dto.getHighlight());
		
		postRepo.save(post);
	}
	
	public List<Post> readOnlyHighlight() {
		List<Post> posts = postRepo.searchOnlyHighlight();
		
		return posts;
	}
	
	public Post read(Long id) {
		Post post = postRepo.findById(id).orElseThrow();
		
		return post;
	}
	
	public void deletePostById(Long id) {
		postRepo.deleteById(id);
	}
	
	public void updatePost(PostUpdateDto dto) {
		Post post = postRepo.findById(dto.getId()).orElseThrow();
		
		Post updatePost = post.update(dto.getTitle(), dto.getContent());
		
		postRepo.save(updatePost);
	}
}
