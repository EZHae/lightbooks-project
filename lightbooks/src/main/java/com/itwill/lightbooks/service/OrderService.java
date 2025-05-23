package com.itwill.lightbooks.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.itwill.lightbooks.domain.CoinPayment;
import com.itwill.lightbooks.domain.CoinPaymentWaiting;
import com.itwill.lightbooks.domain.TicketPayment;
import com.itwill.lightbooks.dto.CoinApproveResponse;
import com.itwill.lightbooks.dto.CoinReadyResponse;
import com.itwill.lightbooks.dto.EpisodeBuyDto;
import com.itwill.lightbooks.dto.PaymentRequestDto;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentRepository;
import com.itwill.lightbooks.repository.coinpayment.CoinPaymentWaitingRepository;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;
import com.itwill.lightbooks.repository.ticketpayment.TicketPaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final RestTemplate restTemplate;
	private final CoinPaymentRepository coinPaymentRepo;
	private final CoinPaymentWaitingRepository coinPaymentWaitingRepo;
	private final TicketPaymentRepository ticketPaymentRepo;
	private final NovelRepository novelRepo;
	private final EpisodeRepository epiRepo;
	
    private final String SECRET_KEY = "DEV_SECRET_KEY DEVEE3295B3CF7D02C946B4565AD192895823EB5";
    private final String CONTENT_TYPE = "application/json";
    private final String CID = "TC0ONETIME"; // 테스트 CID
    private String APPROVAL_URL = "http://35.230.97.96:8080/order/kakaopay/approve"; // 결제 완료
    private String CANCEL_URL = "http://35.230.97.96:8080/order/kakaopay/cancel";
    private String FAIL_URL = "http://35.230.97.96:8080/order/kakaopay/fail";
    
    public CoinReadyResponse payReady(PaymentRequestDto dto, String partnerOrderId) {
    	log.info("payReady(PaymentRequestDto={})", dto);
    	
    	Map<String, String> parameters = new HashMap<String, String>();
    	parameters.put("cid", CID);
    	parameters.put("partner_order_id", partnerOrderId);
    	parameters.put("partner_user_id", dto.getUserId().toString());
    	parameters.put("item_name", "라잇북스: " + dto.getCoin().toString() + "코인");
    	parameters.put("quantity", "1");
    	parameters.put("total_amount", dto.getCash().toString());
    	parameters.put("vat_amount", "0");
    	parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", APPROVAL_URL); // 결제 성공 시 URL
        parameters.put("cancel_url", CANCEL_URL);      // 결제 취소 시 URL
        parameters.put("fail_url", FAIL_URL);          // 결제 실패 시 URL
    	
    	HttpEntity<Map<String, String>> requestEntity = new HttpEntity<Map<String,String>>(parameters, this.getHeaders());
    	
    	String url = "https://open-api.kakaopay.com/online/v1/payment/ready";
    	ResponseEntity<CoinReadyResponse> responseEntity = restTemplate.postForEntity(url, requestEntity, CoinReadyResponse.class);
    	
    	log.info("결제준비 응답객체: " + responseEntity.getBody());
    	return responseEntity.getBody();
    }
    
    public CoinApproveResponse payApprove(String tid, String pgToken, String userId, String partnerOrderId) {
    	log.info("payApprove(tid={}, pgToken={}, partnerORderId={}", tid, pgToken, partnerOrderId);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("cid", CID);              // 가맹점 코드(테스트용)
        parameters.put("tid", tid);                       // 결제 고유번호
        parameters.put("partner_order_id", partnerOrderId); // 주문번호
        parameters.put("partner_user_id", userId);    // 회원 아이디
        parameters.put("pg_token", pgToken);              // 결제승인 요청을 인증하는 토큰
        
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
        
        String url = "https://open-api.kakaopay.com/online/v1/payment/approve";
        CoinApproveResponse approveResponse = restTemplate.postForObject(url, requestEntity, CoinApproveResponse.class);
        log.info("결제승인 응답객체: " + approveResponse);
        return approveResponse;
    }
    
    private HttpHeaders getHeaders() {
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", SECRET_KEY);
    	headers.set("Content-type", CONTENT_TYPE);
    	
    	return headers;
    }
    
    public CoinPayment saveCoinPayment(CoinPayment coinPayment) {
    	CoinPayment savedCoinPayment = coinPaymentRepo.save(coinPayment);
    	
    	return savedCoinPayment;
    }
    
    public CoinPayment saveCoinPaymentFromEpisodeBuyDto(EpisodeBuyDto dto) {
    	CoinPayment coinpayment = CoinPayment.builder()
    			.userId(dto.getUserId())
    			.novel(novelRepo.findById(dto.getNovelId()).orElseThrow())
    			.episodeId(dto.getEpisodeId())
    			.coin(dto.getCoin())
    			.type(1)
    			.novelTitle(dto.getNovelTitle())
    			.episodeNum(dto.getEpisodeNum()).build();
    	
    	CoinPayment savedCoinPayment = coinPaymentRepo.save(coinpayment);
    	
    	return savedCoinPayment;
    }
    
    public CoinPaymentWaiting saveCoinPaymentWaiting(CoinPaymentWaiting coinPaymentWaiting) {
    	CoinPaymentWaiting savedCoinPaymentWaiting = coinPaymentWaitingRepo.save(coinPaymentWaiting);
    	
    	return savedCoinPaymentWaiting;
    }
    
    @Transactional
    public Page<CoinPayment> readCoinPaymentByUserId(Long userId, int page, int size, int type) {
    	Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
    	Page<CoinPayment> result = coinPaymentRepo.findByUserIdAndType(userId, type, pageable);
    	
    	return result;
    }
    
    @Transactional
    public Page<CoinPaymentWaiting> readCoinPaymentWaitingByUserId(Long userId, int page, int size, int type) {
    	Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
    	Page<CoinPaymentWaiting> result = coinPaymentWaitingRepo.findByUserIdAndType(userId, type, pageable);
    	
    	return result;
    }
    
    public TicketPayment saveTicketPaymentFromEpisodeBuyDto(EpisodeBuyDto dto, Long ticketId) {
    	TicketPayment ticketPayment = TicketPayment.builder()
    			.ticketId(ticketId)
    			.userId(dto.getUserId())
    			.novelId(dto.getNovelId())
    			.episodeId(dto.getEpisodeId())
    			.novelTitle(dto.getNovelTitle())
    			.episodeNum(dto.getEpisodeNum()).build();
    	
    	TicketPayment savedTicketPayment = ticketPaymentRepo.save(ticketPayment);
    	
    	return savedTicketPayment;
    }
}
