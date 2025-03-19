package com.itwill.lightbooks.repository.coinpayment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.CoinPaymentWaiting;

public interface CoinPaymentWaitingRepository extends JpaRepository<CoinPaymentWaiting, Long> {

	Page<CoinPaymentWaiting> findByUserIdAndType(Long userId, int type, Pageable pageable);
}
