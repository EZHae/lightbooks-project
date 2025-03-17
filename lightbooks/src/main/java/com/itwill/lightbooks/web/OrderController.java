package com.itwill.lightbooks.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.CoinPayment;
import com.itwill.lightbooks.domain.CoinPaymentWaiting;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.CoinApproveResponse;
import com.itwill.lightbooks.dto.CoinReadyResponse;
import com.itwill.lightbooks.dto.PaymentRequestDto;
import com.itwill.lightbooks.service.OrderService;
import com.itwill.lightbooks.service.UserService;
import com.itwill.lightbooks.utils.SessionUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

	private final OrderService orderService;
	private final UserService userService;
	
	
	@PostMapping("/kakaopay/ready")
	public ResponseEntity<CoinReadyResponse> payReady(@RequestBody PaymentRequestDto dto, HttpSession session) {
		log.info("payReady(PaymentRequestDto={})", dto);
		
		String partnerOrderId = UUID.randomUUID().toString();
		session.setAttribute("partnerOrderId", partnerOrderId);
		
		CoinReadyResponse readyResponse = orderService.payReady(dto, partnerOrderId);
		log.info("readyResponse={}", readyResponse);
		
		session.setAttribute("tid", readyResponse.getTid());
		session.setAttribute("userId", dto.getUserId());
		
		log.info("tid={}, userId={}, partnerOrderId={}", readyResponse.getTid(), dto.getUserId(), partnerOrderId);
		
		CoinPayment coinPayment = CoinPayment.builder().userId(dto.getUserId()).type(0).coin(dto.getCoin()).cash(dto.getCash()).build();
		
		session.setAttribute("coinPayment", coinPayment);
		
		return ResponseEntity.ok(readyResponse);
	}
	
	@GetMapping("/kakaopay/approve")
	public String payApprove(@RequestParam(name = "pg_token") String pgToken, HttpSession session, Model model) {
		log.info("payApprove()");
		
		String tid = session.getAttribute("tid").toString();
		String userId = session.getAttribute("userId").toString();
		String partnerOrderId = session.getAttribute("partnerOrderId").toString();
		log.info("pgToken={}, tid={}, userId={}, partnerOrderId={}", pgToken, tid, userId, partnerOrderId);
		
		CoinApproveResponse approveResponse = orderService.payApprove(tid, pgToken, userId, partnerOrderId);
		log.info("approveResponse= {}", approveResponse);
		
		if (approveResponse != null) {
			model.addAttribute("approveResponse", approveResponse);
			CoinPayment coinPayment = (CoinPayment) session.getAttribute("coinPayment");
			orderService.saveCoinPayment(coinPayment);
		}
		
		return "/user/pay-success";
	}
	
	@PostMapping("/transfer/ready")
		public ResponseEntity<CoinPaymentWaiting> transferReady(@RequestBody PaymentRequestDto dto) {
		log.info("tranferReady(PaymentRequestDto={})", dto);
		User user = userService.searchById(dto.getUserId()); 
		
		CoinPaymentWaiting coinPaymentWaiting = CoinPaymentWaiting.builder().user(user).type(0).coin(dto.getCoin()).cash(dto.getCash()).con(0).build();
		log.info("coinPaymentWaiting={}", coinPaymentWaiting);
		orderService.saveCoinPaymentWaiting(coinPaymentWaiting);
			
		return ResponseEntity.ok(coinPaymentWaiting);
	}
	
}
