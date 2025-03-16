package com.itwill.lightbooks.dto;

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
	private String coverSrc;
	private Integer likeCount;
	private Integer state;
	private String genres;
	private LocalDateTime modifiedTime;
	
	public static NovelListItemDto fromEntity(Novel entity) {
		String firstGenre = "";
		
		for(NGenre genre : entity.getNovelGenre()) {
			firstGenre = genre.getGenre().getName();
			break;
		}
		
		return NovelListItemDto.builder()
				.id(entity.getId())
				.title(entity.getTitle())
				.writer(entity.getWriter())
				.coverSrc(entity.getCoverSrc())
				.likeCount(entity.getLikeCount())
				.state(entity.getState())
				.genres(firstGenre)
				.modifiedTime(entity.getModifiedTime())
				.build();
	}
}
