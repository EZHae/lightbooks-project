package com.itwill.lightbooks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itwill.lightbooks.domain.CoinPaymentWaiting;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentRepository;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentWaitingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

	private final CoinPaymentRepository coinPaymentRepo;
	private final CoinPaymentWaitingRepository coinPaymentWaitingRepo;
	
	public List<CoinPaymentWaiting> searchAllCoinPaymentWaiting() {
		List<CoinPaymentWaiting> waitings = coinPaymentWaitingRepo.findAll();
		
		return waitings;
	}
	
	public CoinPaymentWaiting searchCoinPaymentWaitingById(Long id) {
		CoinPaymentWaiting coinPaymentWaiting = coinPaymentWaitingRepo.findById(id).orElseThrow();
		
		return coinPaymentWaiting;
	}
}
