package com.itwill.lightbooks.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "NOVELS")
public class Novel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String title; // 소설 제목
	
	private String intro; // 소개글 
	
	private int likeCount; // 좋아요 수
	
	private String coverSrc; // 표지 이미지 URL
	
	private int grade; // 작품 등급 (예: 1~5점)
	
	private int ageLimit; // 연령 제한 (0: 전체이용가, 1: 성인)
	
	private String days; // 연재 요일 (예: "월, 수, 금")
	
	private int state; // 연재 상태 (1: 연재중, 0: 완결)
	
}
