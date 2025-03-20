package com.itwill.lightbooks.domain;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

@DynamicInsert
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
	private Long id;
	
	@Basic(optional = false)
	private String title; // 소설 제목
	
	@Basic(optional = false)
	private String intro; // 소개글 
	
	@Column(name = "user_id")
	@Basic(optional = false)
	private Long userId; // 유저 아이디
	
	@Basic(optional = false)
	private String writer; // 작성자
	
	@Column(name = "like_count")
	private Integer likeCount; // 좋아요 수
	
	@Column(name = "cover_src")
	@Basic(optional = false)
	private String coverSrc; // 표지 이미지 URL
	
	private Integer grade;
	
	private BigDecimal rating;
	
	@Column(name = "age_limit")
	@Basic(optional = false)
	private Integer ageLimit; // 연령 제한 (0: 전체이용가, 1: 15세이상 2: 19세이상)
	
	@Basic(optional = false)
	private String days; // 연재 요일 (예: "월, 수, 금")
	
	private Integer state; // 연재 상태 (1: 연재중, 0: 완결)
	
	@OneToMany(mappedBy = "novel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//	@JoinColumn(name = "novel_id")
	@ToString.Exclude
	private List<NGenre> novelGenre;

	public Novel update(String title, String intro, String coverSrc, Integer ageLimit, String days, Integer state) {
	    this.title = title;
	    this.intro = intro;
	    this.coverSrc = coverSrc;
	    this.ageLimit = ageLimit;
	    this.days = days;
	    this.state = state;
	    
	    return this;
	}
	
	public void setNovelGenre(List<NGenre> novelGenre) {
		this.novelGenre = novelGenre;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public void updateGrade(Integer grade) {
		this.grade = grade;
	}

}
