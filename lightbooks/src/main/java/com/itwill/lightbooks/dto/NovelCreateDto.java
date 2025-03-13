package com.itwill.lightbooks.dto;

import java.util.List;

import com.itwill.lightbooks.domain.Novel;

import lombok.Data;

@Data
public class NovelCreateDto {
	
	private String title;
	private String intro;
	private String writer;
	private String coverSrc;
	private Integer ageLimit;
	private List<String> days;
	
	public Novel toEntity() {
		return Novel.builder()
				.title(title).intro(intro).writer(writer).coverSrc(coverSrc).ageLimit(ageLimit)
				.days(days != null ? String.join(",", days) : "비정기")
				.build();
	}
}
