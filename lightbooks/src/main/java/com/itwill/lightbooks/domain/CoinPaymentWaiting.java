package com.itwill.lightbooks.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
//@ToString
@EqualsAndHashCode
@Entity
@Table(name = "COIN_PAYMENT_WAITING")
public class CoinPaymentWaiting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	private int type;
	private Long coin;
	private Long cash;
	
	@Column(name = "created_time", nullable = false, updatable = false, insertable = false)
	private LocalDateTime createdTime;
	
	private int con;
	
	public CoinPaymentWaiting update(int con) {
		this.con = con;
		return this;
	}
	
	@Override
	public String toString() {
	    return "CoinPaymentWaiting(id=" + id + 
	           ", userId=" + (user != null ? user.getId() : "null") + 
	           ", type=" + type + 
	           ", coin=" + coin + 
	           ", cash=" + cash + 
	           ", createdTime=" + createdTime + 
	           ", con=" + con + ")";
	}
}
