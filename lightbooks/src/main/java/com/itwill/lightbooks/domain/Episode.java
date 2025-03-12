package com.itwill.lightbooks.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@Getter
@ToString(callSuper = true) //Episode의 toString() 메서드를 작성할 때, 상위 클래스(BaseTimeEntity)의 toString()을 호출하겠다는 말.
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EPISODES")
public class Episode extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//관계 매핑
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "novel_id", nullable = false)
	private Novel novel;
	
	@Column(name = "episode_num")
	private Integer episodeNum;
	
	@Column(nullable = false)
	private String title;
	
	@Lob // mysql 타입이 text라서
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = false)
	private Integer views = 0;
	
	@Column(nullable = false)
	private Integer category; // 구분(0: 공지, 1:무료, 2:유료)
	
	//조회수 증가 메서드
	public void increaseViews() {
		this.views++;
	}
	
}
