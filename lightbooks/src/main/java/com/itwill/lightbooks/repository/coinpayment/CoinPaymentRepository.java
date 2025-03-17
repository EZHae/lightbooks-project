package com.itwill.lightbooks.repository.coinpayment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.CoinPayment;

public interface CoinPaymentRepository extends JpaRepository<CoinPayment, Long> {

	
}
