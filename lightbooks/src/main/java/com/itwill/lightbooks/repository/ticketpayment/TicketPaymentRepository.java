package com.itwill.lightbooks.repository.ticketpayment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.TicketPayment;

public interface TicketPaymentRepository extends JpaRepository<TicketPayment, Long> {

	Page<TicketPayment> findByUserId(Long userId, Pageable pageable);
}
