package com.itwill.lightbooks.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// 소설 전체 검색 메서드에서 Controller에 전달할 dto
// 목록 페이지에서 보여줄 데이터들만 선별.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	
	// 절대로 entity.getNovelGenre() 직접 접근하지 말고,
	// 쿼리에서 직접 장르명을 바로 주입하도록 작성해야 합니다.
	public static NovelListItemDto fromEntity(Novel entity, String genreName, Long totalViews) {
		return NovelListItemDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.writer(entity.getWriter())
				.intro(entity.getIntro())
				.coverSrc(entity.getCoverSrc())
				.likeCount(entity.getLikeCount())
				.state(entity.getState())
				.genres(genreName) // 장르를 인자로 받기
				.rating(entity.getRating())
				.grade(entity.getGrade())
				.ageLimit(entity.getAgeLimit())
				.modifiedTime(entity.getModifiedTime())
				.totalViews(totalViews)
				.build();
	}
	
	// 기존 메서드 (조회수 없는 버전)
	public static NovelListItemDto fromEntity(Novel entity, String genreName) {
		return fromEntity(entity, genreName, 0L);
	}
}
