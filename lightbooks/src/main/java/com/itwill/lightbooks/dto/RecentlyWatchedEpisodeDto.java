package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class RecentlyWatchedEpisodeDto {
	
	private Long episodeId;
	
	private Integer episodeNum;
	
	private String episodeTitle;
	
	private LocalDateTime accessTime;
	
	private String novelTitle;
	
	private String novelWriter;
	
	private List<String> novelGenres;
	
	private String coverSrc;
}
