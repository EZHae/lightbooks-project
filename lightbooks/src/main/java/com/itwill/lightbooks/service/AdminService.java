package com.itwill.lightbooks.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.itwill.lightbooks.domain.CoinPaymentWaiting;
import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.dto.NovelSearchDto;
import com.itwill.lightbooks.dto.NovelSearchGradeDto;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentRepository;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentWaitingRepository;
import com.itwill.lightbooks.repository.novel.NovelGradeRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

	private final CoinPaymentRepository coinPaymentRepo;
	private final CoinPaymentWaitingRepository coinPaymentWaitingRepo;
	
	private final NovelGradeRequestRepository novelGradeRequestRepo;
	
	
	public List<CoinPaymentWaiting> searchAllCoinPaymentWaiting() {
		List<CoinPaymentWaiting> waitings = coinPaymentWaitingRepo.findAll();
		
		return waitings;
	}
	
	public CoinPaymentWaiting searchCoinPaymentWaitingById(Long id) {
		CoinPaymentWaiting coinPaymentWaiting = coinPaymentWaitingRepo.findById(id).orElseThrow();
		
		return coinPaymentWaiting;
	}
	
	
	/**
	 * 어드민이 프리미엄 신청 내역을 조회 및 무료/유료 여부
	 * 데이터가 많아질 경우에 대비해 페이징, 검색 추가를 고려
	 */
	public List<NovelGradeRequest> searchAllNovelGradeRequests() {
		List<NovelGradeRequest> gradeRequests = novelGradeRequestRepo.findAll();
		return gradeRequests;
	}
	
	public List<NovelGradeRequest> searchAllNovelGradeRequestByKeyword(NovelSearchGradeDto dto, Sort sort) {
		Pageable pageable = PageRequest.of(dto.getP(), 10, sort);
//		List<NovelGradeRequest> gradeRequests = novelGradeRequestRepo.searchByKeyword(dto,pageable);
		
//		return gradeRequests;
		return null;
	}
	
	public NovelGradeRequest searchNovelGradeRequestByUserIdAndNovelId(Long userId, Long NovelId) {
//		NovelGradeRequest gradeRequest = novelGradeRequestRepo.findByUserIdAndNovelIdAndType(userId, NovelId, 0, null) 
		return null;	
	}
}
