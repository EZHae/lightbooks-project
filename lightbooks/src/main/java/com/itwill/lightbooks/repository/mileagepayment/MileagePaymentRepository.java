package com.itwill.lightbooks.repository.mileagepayment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.MileagePayment;

public interface MileagePaymentRepository extends JpaRepository<MileagePayment, Long> {

}
