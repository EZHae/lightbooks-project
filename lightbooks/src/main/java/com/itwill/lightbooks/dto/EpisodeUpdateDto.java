package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EpisodeUpdateDto {

	private Long id;
	
	private Long novelId;
	
	private String title;
	
	private String content;
}
