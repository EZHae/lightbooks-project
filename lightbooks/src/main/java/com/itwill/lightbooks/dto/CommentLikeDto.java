package com.itwill.lightbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeDto {
	private Long Id;
	private Long episodeId;
	private int likeCount;
}
