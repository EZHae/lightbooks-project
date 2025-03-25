package com.itwill.lightbooks.dto;

import java.util.List;

import lombok.Data;

@Data
public class PurchasedNovelBookmarkDto {

	private Long novelId;
	
    private String novelTitle;
    
    private String coverSrc;
    
    private String novelWriter;
	
	private List<String> novelGenres;
	
	private Long totalViews; //소설 조회수
}
