package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class RecentlyWatchedEpisodeDto {
	
	private Long novelId;
	
	private Long episodeId;
	
	private Integer novelGrade;//
	
	private Integer episodeNum;//
	
	private String episodeTitle;//
	
	private LocalDateTime accessTime;//
	
	private String novelTitle;//
	
	private String novelIntro;//
	
	private String novelWriter;//
	
	private List<String> novelGenres;//
	
	private Integer likeCount;//
	
	private String coverSrc;//
	
	private Long totalViews;//

}
