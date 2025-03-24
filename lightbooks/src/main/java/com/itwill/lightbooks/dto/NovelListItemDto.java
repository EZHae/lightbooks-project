package com.itwill.lightbooks.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


// 소설 전체 검색 메서드에서 Controller에 전달할 dto
// 목록 페이지에서 보여줄 데이터들만 선별.
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NovelListItemDto {
	
	private Long id;
	private String title;
	private String writer;
	private String intro;
	private String coverSrc;
	private Integer likeCount;
	private Integer state;
	private String genres;
	private BigDecimal rating;
	private Integer grade;
	private Integer ageLimit;
	private LocalDateTime modifiedTime;
	
	private Long totalViews; // 회차 총 조회수
	
	public static NovelListItemDto fromEntity(Novel entity, Long totalViews) {
		String firstGenre = "";
		
		for(NGenre genre : entity.getNovelGenre()) {
			firstGenre = genre.getGenre().getName();
			break;
		}
		
		return NovelListItemDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.writer(entity.getWriter())
				.intro(entity.getIntro())
				.coverSrc(entity.getCoverSrc())
				.likeCount(entity.getLikeCount())
				.state(entity.getState())
				.genres(firstGenre)
				.rating(entity.getRating())
				.grade(entity.getGrade())
				.ageLimit(entity.getAgeLimit())
				.modifiedTime(entity.getModifiedTime())
				.totalViews(totalViews)					// 조회수 포함
				.build();
	}
}
