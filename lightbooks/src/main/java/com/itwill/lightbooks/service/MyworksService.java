package com.itwill.lightbooks.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.CoinPayment;
import com.itwill.lightbooks.domain.CoinPaymentIncome;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentIncomeRepository;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentRepository;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyworksService {

	private final CoinPaymentIncomeRepository coinPaymentIncomeRepo;
	private final CoinPaymentRepository coinPaymentRepo;
	private final EpisodeRepository epiRepo;
	private final UserRepository userRepo;
	
	public Page<Object[]> readNovelWithIncome(Long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		log.info("service::pageable={}", pageable);
		
		Page<Object[]> result = coinPaymentIncomeRepo.findByUserId(userId, pageable);
		
		return result;
	}
	
	@Transactional
	public Page<CoinPaymentIncome> readIncome(Long novelId, int page, int size, int type) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
		
		Page<CoinPaymentIncome> result = coinPaymentIncomeRepo.findByNovelIdAndType(novelId, type, pageable);
		if (!result.getContent().isEmpty()) {
			result.getContent().forEach(item -> {
				String nickname = (item.getFromUserId() != null) ? userRepo.findById(item.getFromUserId()).orElseThrow().getNickname() : null;
				item.setNickname(nickname);
			});
		}
		
		return result;
	}
}
