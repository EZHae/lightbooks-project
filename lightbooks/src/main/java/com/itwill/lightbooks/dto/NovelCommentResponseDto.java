package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovelCommentResponseDto {
	
	private Long commentId;
	private Long novelId;
	private Long episodeId;
	private Long userId;
	
	private String text;
	
	private int likeCount;
	private String nickname;
	private int spoiler;
	private boolean likedByUser;
	private String episodeTitle;   // 회차 제목 추가
    private int episodeNum;     // 회차 번호도 같이 넣을 수 있음
    private LocalDateTime modifiedTime;
}
