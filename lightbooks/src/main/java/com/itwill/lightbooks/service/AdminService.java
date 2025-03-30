package com.itwill.lightbooks.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//	private final CoinPaymentRepository coinPaymentRepo;
	private final CoinPaymentWaitingRepository coinPaymentWaitingRepo;
	private final NovelGradeRequestRepository novelGradeRequestRepo;
	
	
	public Page<CoinPaymentWaiting> searchAllCoinPaymentWaiting(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
		Page<CoinPaymentWaiting> result = coinPaymentWaitingRepo.findAll(pageable);
		if (!result.getContent().isEmpty()) {
			result.getContent().forEach(item -> {
				if (item.getUser() != null) {
					item.setUserLoginId(item.getUser().getLoginId());
					item.setUserUsername(item.getUser().getUsername());
				}
			});
		}
		
		return result;
	}
	
	public Page<CoinPaymentWaiting> searchCoinPaymentWaitingByCon(int page, int size, int con) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
		Page<CoinPaymentWaiting> result = coinPaymentWaitingRepo.findByCon(con, pageable);
		if (!result.getContent().isEmpty()) {
			result.getContent().forEach(item -> {
				if (item.getUser() != null) {
					item.setUserLoginId(item.getUser().getLoginId());
					item.setUserUsername(item.getUser().getUsername());
				}
			});
		}
		
		return result;
	}	
	
	public CoinPaymentWaiting searchCoinPaymentWaitingById(Long id) {
		CoinPaymentWaiting coinPaymentWaiting = coinPaymentWaitingRepo.findById(id).orElseThrow();
		
		return coinPaymentWaiting;
	}
	
	
	/**
	 * 어드민이 프리미엄 신청 내역을 조회 및 무료/유료 여부
	 * 데이터가 많아질 경우에 대비해 페이징, 검색 추가를 고려
	 */
	// 모든 신청 내역 리스트
	public List<NovelGradeRequest> searchAllNovelGradeRequests() {
		List<NovelGradeRequest> gradeRequests = novelGradeRequestRepo.findAll();
		return gradeRequests;
	}
	
	// 키워드로 검색하는 내역을 리스트로 반환
	public Page<NovelGradeRequest> searchAllNovelGradeRequestByKeyword(NovelSearchGradeDto dto, Sort sort) {
		Pageable pageable = PageRequest.of(dto.getP(), 10, sort);
		Page<NovelGradeRequest> gradeRequests = novelGradeRequestRepo.searchByKeyword(dto, pageable);
		
		return gradeRequests;
	}
	
	// 아이디와 소설아이디로 신청한 목록 1개를 조회
	@Transactional
	public NovelGradeRequest searchNovelGradeRequestById(Long id) {
		NovelGradeRequest gradeReqest = novelGradeRequestRepo.findById(id).orElseThrow();
		
		return gradeReqest;
	}

	public void saveGrade(NovelGradeRequest updateGrade) {
		novelGradeRequestRepo.save(updateGrade);
	}
}
