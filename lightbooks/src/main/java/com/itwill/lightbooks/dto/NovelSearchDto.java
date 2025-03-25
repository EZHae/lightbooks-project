package com.itwill.lightbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovelSearchDto {
	
	private String category; //검색 카테고리
	private String keyword; // 검색어
	private int p; // 페이지
}
