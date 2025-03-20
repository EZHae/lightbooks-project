package com.itwill.lightbooks.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.CoinPayment;
import com.itwill.lightbooks.domain.CoinPaymentWaiting;
import com.itwill.lightbooks.service.AdminService;
import com.itwill.lightbooks.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	
	private final AdminService adminService;
	private final OrderService orderService;

	@PreAuthorize("isAuthenticated() and principal.loginId == 'admin'")  // 로그인된 계정의 login_id가 admin일 때만 접근 가능
	@GetMapping("/waitingpayment")
	public String waitingpayment(Model model) {
		
		List<CoinPaymentWaiting> waitings = adminService.searchAllCoinPaymentWaiting();
		model.addAttribute("waitings", waitings);
		
		return "/admin/waiting-payment";
	}
	
	@ResponseBody
	@PostMapping("/waitingpayment/check")
	public ResponseEntity<?> waitingpaymentCheck(@RequestParam(name = "id") Long id) {
		
		// 선택된 id의 paymentWaiting 처리상태 변경
		CoinPaymentWaiting waiting = adminService.searchCoinPaymentWaitingById(id);
		CoinPaymentWaiting updatedWaiting = waiting.update(1);
		
		// 처리한 결과 payment 추가
		CoinPayment coinPayment = CoinPayment.builder().userId(updatedWaiting.getUser().getId()).coin(updatedWaiting.getCoin()).cash(updatedWaiting.getCash()).type(0).build();
		orderService.saveCoinPayment(coinPayment);
		
		return ResponseEntity.ok(null);
	}
	
	
	// 유료 무료 전환 프리미엄 페이지
	@PreAuthorize("isAuthenticated() and principal.loginId == 'admin'") // 로그인된 계정의 login_id가 admin일 때만 접근 가능
	@GetMapping("/premiumrequest")
	public String premiumrequest(Model model) {
		
		List<CoinPaymentWaiting> waitings = adminService.searchAllCoinPaymentWaiting();
		model.addAttribute("waitings", waitings);
		
		return "/admin/premium-request";
	}
}
