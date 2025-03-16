package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;

import com.itwill.lightbooks.domain.Episode;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EpisodeListDto {

	private Long id;

	private Long novelId;

	private Integer episodeNum;

	private String title;

	private Integer views;

	private Integer category; // 0:공지, 1:무료, 2:유료

	private LocalDateTime createdTime;
	
}
