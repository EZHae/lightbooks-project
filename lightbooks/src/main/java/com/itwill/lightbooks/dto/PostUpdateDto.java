package com.itwill.lightbooks.dto;

import lombok.Data;

@Data
public class PostUpdateDto {

	private Long id;
	private String title;
	private String content;
}
