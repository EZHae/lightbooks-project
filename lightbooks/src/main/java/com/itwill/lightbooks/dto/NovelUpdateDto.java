package com.itwill.lightbooks.dto;

import java.util.List;

import com.itwill.lightbooks.domain.Novel;

import lombok.Data;

@Data
public class NovelUpdateDto {
	
	private Long id;
	private String title;
	private String intro;
	private String coverSrc;
	private Integer ageLimit;
	private Integer state;
	private List<String> days;
	
	private Integer genreId;
	
	
}
