package com.itwill.lightbooks.repository.coinpayment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.CoinPayment;

public interface CoinPaymentRepository extends JpaRepository<CoinPayment, Long> {

	Page<CoinPayment> findByUserIdAndType(Long userId, int type, Pageable pageable);
	
}
