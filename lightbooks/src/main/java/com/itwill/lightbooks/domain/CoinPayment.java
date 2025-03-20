package com.itwill.lightbooks.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@Table(name = "COIN_PAYMENT")
public class CoinPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_id")
	private Long userId;
	private int type;
	
	@Column(name = "novel_id")
	private Long novelId;
	
	@Column(name = "episode_id")
	private Long episodeId;
	
	@Column(name = "created_time", nullable = false, updatable = false, insertable = false)
	private LocalDateTime createdTime;
	private Long coin;
	private Long cash;
	
	@Column(name = "donation_msg")
	private String donationMsg;
	
}
