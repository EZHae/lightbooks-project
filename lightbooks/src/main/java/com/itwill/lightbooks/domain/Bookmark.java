package com.itwill.lightbooks.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DynamicInsert
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "BOOKMARK")
public class Bookmark {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//관계 매핑
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	//관계 매핑
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "novel_id", nullable = false)
	private Novel novel;
	
	//관계 매핑
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "episode_id")
	private Episode episode;
	
	@Column(nullable = false)
	private Integer type; //0: 좋아요 누른 작품, 1: 최근 본 회차, 2: 구매 작품, 3: 알림 설정
	

	@Column(name = "access_time", nullable = false, insertable = false)
	private LocalDateTime accessTime;
	
	@CreatedDate
	@Column(name = "created_time", nullable = false)//수정
	private LocalDateTime createdTime;
	
	// accessTime 업데이트 메서드
    public Bookmark updateAccessTime(LocalDateTime accessTime) {
        this.accessTime = accessTime;
        
        return this;
    }
}
