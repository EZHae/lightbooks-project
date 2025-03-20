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
@Table(name = "TICKET_PAYMENT")
public class TicketPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "ticket_id")
	private Long ticketId;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "novel_id")
	private Long novelId;
	
	@Column(name = "episode_id")
	private Long episodeId;
	
	@Column(name = "created_time", nullable = false, updatable = false, insertable = false)
	private LocalDateTime createdTime;
}
