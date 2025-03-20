package com.itwill.lightbooks.dto;

import com.itwill.lightbooks.domain.Novel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovelItemDto {

	private Long id;
	private Long userId;
	private String title;
	private String writer;
	private Integer grade;
	
	public static NovelItemDto fromEntity(Novel novel) {
		if (novel != null) {
			return NovelItemDto.builder()
					.id(novel.getId())
					.userId(novel.getUserId())
					.title(novel.getTitle())
					.writer(novel.getWriter())
					.grade(novel.getGrade())
					.build();
		} else {
			return null;
		}
	}
}
