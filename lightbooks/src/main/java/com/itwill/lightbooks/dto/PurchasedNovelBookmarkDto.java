package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PurchasedNovelBookmarkDto {

	private Long novelId;
	
	private Integer novelGrade;
	
    private String novelTitle;
    
    private String novelIntro;
    
    private String coverSrc;
    
    private String novelWriter;
	
	private List<String> novelGenres;
	
	private LocalDateTime purchasedDate; // 구매 날짜 필드 추가
	
	private Integer likeCount;
	
	private Long totalViews; //소설 조회수
}
