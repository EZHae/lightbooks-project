package com.itwill.lightbooks.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "COMMENTS")
public class Comment extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//관계매핑
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	//관계매핑
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "novel_id", nullable = false)
	private Novel novel;
	
	//관계매핑
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "episode_id")
	private Episode episode;
	
	@Column(nullable = false)
	private String text;
	
    @Column(nullable = false)
    private String nickname;

    @Column(name = "nickname", nullable = false)
    private String usernickname;
	
	@Column(name = "like_count", nullable = false)
	private int likeCount = 0;
	
	@Column(nullable = false)
	private int spoiler = 0;
}
