package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;

import lombok.Data;

@Data
public class EpisodeCreateDto {

	private Long novelId;
	
	private Integer episodeNum;
	
	private String title;
	
	private String content;
	
	private Integer category;
	
	private LocalDateTime reservationTime;
	
	//DTO -> ENTITY객체로
	public Episode toEntity(Novel novel) {
		return Episode.builder()
				.novel(novel)
				.episodeNum(episodeNum)
				.title(title)
				.content(content)
				.category(category)
				.reservationTime(reservationTime)
				.build();
	}
}
