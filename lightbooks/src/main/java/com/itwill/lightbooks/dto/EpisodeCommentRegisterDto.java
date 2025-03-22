package com.itwill.lightbooks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
	
	@NotBlank(message = "댓글을 입력해주세요.")
    @Size(max = 200, message = "댓글은 200자 이내로 작성해주세요.")
	private String text;
	
	private int likeCount;
	private String nickname;
	private int spoiler;
	
}
