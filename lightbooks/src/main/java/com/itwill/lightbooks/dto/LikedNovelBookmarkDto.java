package com.itwill.lightbooks.dto;

import java.util.List;

import lombok.Data;

@Data
public class LikedNovelBookmarkDto {

	private Long novelId;
	
    private String novelTitle;
    
    private String coverSrc;
    
    private Integer likeCount;

    private String novelwriter; 
    
    private List<String> novelGenres;
    
    private Long totalViews; //소설 조회수

}
