package com.itwill.lightbooks.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NovelLikeId implements Serializable {
	// 다중 복합 키를 사용하기 위한 클래스
	
	private static final long serialVersionUID = 1L;
	
	private Long user;
	private Long novel;
	
}
