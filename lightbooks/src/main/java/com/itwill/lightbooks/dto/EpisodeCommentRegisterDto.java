package com.itwill.lightbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeCommentRegisterDto {

	private Long novelId;
	private Long episodeId;
	private Long userId;
	
	private String text;
	
	private int likeCount;
	private String nickname;
	private int spoiler;
	
}
