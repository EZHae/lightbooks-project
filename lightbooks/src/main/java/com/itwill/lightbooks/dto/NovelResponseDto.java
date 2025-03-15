package com.itwill.lightbooks.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NovelResponseDto {
	
	private Long id;
	private String title;
	private String intro;
	private String writer;
	private String coverSrc;
	private Integer likeCount;
	private Integer state;
	private List<String> genres;
	
	
	
}
