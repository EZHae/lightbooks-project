package com.itwill.lightbooks.dto;

import lombok.Data;

@Data
public class EpisodeBuyDto {
	
	// BOOKMARK 테이블 insert에 필요한 값
	private Long userId;
	private Long novelId;
	private Long episodeId;
	
	// COIN_PAYMENT 테이블 insert에 필요한 값
	// COIN_PAYMENT 테이블에서도 userId, novelId, episodeId 씀
	private Long coin;
	
	/*▲▲ 실질적으로 DB에 들어가는 값들 ▲▲*/
	
	// 타입에 맞는 서비스를 호출하기 위해 필드 설정
	private int type; // (0: 글로벌 이용권으로 구매, 1: 개별 이용권으로 구매, 2: 코인으로 구매)
}
