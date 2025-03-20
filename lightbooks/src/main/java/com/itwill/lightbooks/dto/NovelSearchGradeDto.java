package com.itwill.lightbooks.dto;

import lombok.Data;

@Data
public class NovelSearchGradeDto {

	private String category; //검색 카테고리
	private String keyword; // 검색어
	private int p; // 페이지
}
