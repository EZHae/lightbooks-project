package com.itwill.lightbooks.dto;

import java.util.ArrayList;
import java.util.List;

import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;

import lombok.Data;

@Data
public class NovelCreateDto {
	
	private Integer userId;
	private String title;
	private String intro;
	private String writer;
	private String coverSrc;
	private Integer ageLimit;
	private List<String> days;
	private Integer grade = 0;
	private Integer likeCount = 0;
	private Integer state = 1;
	
	private Integer mainGenreId;
	private List<Integer> novelGenreIds = new ArrayList<>();
	
	public Novel toEntity() {
		return Novel.builder()
				.userId(userId)
				.title(title).intro(intro).writer(writer).coverSrc(coverSrc).ageLimit(ageLimit).grade(grade).likeCount(likeCount).state(state)
				.days(days != null ? String.join(",", days) : "비정기")
				.build();
	}
}
