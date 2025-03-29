package com.itwill.lightbooks.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NovelResponseDto {
	
	private Long id;
	private String title;
	private String intro;
	private String writer;
	private String coverSrc;
	private Integer likeCount;
	private Integer state;
	private Integer grade;
	private List<String> genres;
	private BigDecimal rating;
	
	private Long totalViews; // 회차 총 조회수
	
}
