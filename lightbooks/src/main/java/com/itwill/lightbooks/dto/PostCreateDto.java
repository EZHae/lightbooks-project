package com.itwill.lightbooks.dto;

import com.itwill.lightbooks.domain.Post;

import lombok.Data;

@Data
public class PostCreateDto {

	private String title;
	private String writer;
	private String content;
	
	public Post toEntity() {
		
		return Post.builder().title(title).writer(writer).content(content).highlight(0).build();
	}
}
