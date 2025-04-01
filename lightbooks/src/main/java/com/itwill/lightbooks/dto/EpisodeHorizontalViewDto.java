package com.itwill.lightbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EpisodeHorizontalViewDto {
    private Long episodeId;
    private String episodeTitle;
    private String content;
    private Integer episodeNum;
    private String novelTitle;
    private Integer pageNumber;     // 가로보기 페이지 번호 (1, 2, 3...)
    private String coverSrc;
}
	