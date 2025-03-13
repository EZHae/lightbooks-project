package com.itwill.lightbooks.domain;

import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "NOVELS")
public class Novel extends BaseTimeEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Basic(optional = false)
	private String title; // 소설 제목
	
	@Basic(optional = false)
	private String intro; // 소개글 
	
	@Basic(optional = false)
	private String writer; // 작성자
	
	@Basic(optional = false)
	private int likeCount; // 좋아요 수
	
	@Basic(optional = false)
	private String coverSrc; // 표지 이미지 URL
	
	@Basic(optional = false)
	private int grade; // 작품 등급 (예: 1~5점)
	
	@Basic(optional = false)
	private int ageLimit; // 연령 제한 (0: 전체이용가, 1: 성인)
	
	@Basic(optional = false)
	private String days; // 연재 요일 (예: "월, 수, 금")
	
	@Basic(optional = false)
	private int state; // 연재 상태 (1: 연재중, 0: 완결)
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "novel_id")
	@Basic(optional = false)
	private List<NGenre> novelGenre;
}
