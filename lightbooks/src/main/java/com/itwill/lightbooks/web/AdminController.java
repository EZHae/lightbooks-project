package com.itwill.lightbooks.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.ChatMessage;
import com.itwill.lightbooks.domain.CoinPayment;
import com.itwill.lightbooks.domain.CoinPaymentWaiting;
import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.domain.Post;
import com.itwill.lightbooks.dto.ChatRoomWithIsReadDto;
import com.itwill.lightbooks.dto.PostCreateDto;
import com.itwill.lightbooks.dto.PostUpdateDto;
import com.itwill.lightbooks.dto.PostUpdateHighlightDto;
import com.itwill.lightbooks.dto.PremiumRequestDto;
import com.itwill.lightbooks.service.AdminService;
import com.itwill.lightbooks.service.ChatService;
import com.itwill.lightbooks.service.NovelService;
import com.itwill.lightbooks.service.OrderService;
import com.itwill.lightbooks.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	
	private final AdminService adminService;
	private final OrderService orderService;
	private final NovelService novelService;
	private final PostService postService;
	private final ChatService chatService;

	@PreAuthorize("isAuthenticated() and principal.loginId == 'admin'")  // 로그인된 계정의 login_id가 admin일 때만 접근 가능
	@GetMapping("/waitingpayment")
	public String waitingpayment() {
		
		return "admin/waiting-payment";
	}
	
	@ResponseBody
	@PostMapping("/waitingpayment/check")
	public ResponseEntity<?> waitingpaymentCheck(@RequestParam(name = "id") Long id) {
		
		// 선택된 id의 paymentWaiting 처리상태 변경
		CoinPaymentWaiting waiting = adminService.searchCoinPaymentWaitingById(id);
		CoinPaymentWaiting updatedWaiting = waiting.update(1);
		
		// 처리한 결과 payment 추가
		if (waiting.getType() == 0) {
			CoinPayment coinPayment = CoinPayment.builder().userId(updatedWaiting.getUser().getId()).coin(updatedWaiting.getCoin()).cash(updatedWaiting.getCash()).type(0).build();
			log.info("savedCoinPayment={}", coinPayment);
			orderService.saveCoinPayment(coinPayment);
		}
		orderService.saveCoinPaymentWaiting(updatedWaiting);
		
		return ResponseEntity.ok(null);
	}
	
	// 유료/무료 전환 프리미엄 페이지
	@PreAuthorize("isAuthenticated() and principal.loginId == 'admin'") // 로그인된 계정의 login_id가 admin일 때만 접근 가능
	@GetMapping("/premiumrequest")
	public String premiumrequest() {
		return "admin/premium-request";
	}
	// 유료/무료 신청 확인 시
	@PostMapping("/premiumrequest/check")
	public ResponseEntity<?> premiumCheck(@RequestParam(name = "id") Long id) {
		NovelGradeRequest gradeReq = adminService.searchNovelGradeRequestById(id);
		
		gradeReq.updateStatus(1);
		// 처리한 결과가 Novel grade에 업데이트 
		novelService.updateNovelGrade(gradeReq.getNovel().getId(), gradeReq.getStatus());
		
		return ResponseEntity.ok(null);
	}
	// 유료/무료 신청 취소 시
	@PostMapping("/premiumrequest/cancle")
	public ResponseEntity<?> premiumCancle(@RequestParam(name = "id") Long id) {
		NovelGradeRequest gradeReq = adminService.searchNovelGradeRequestById(id);
		NovelGradeRequest updateGrade = gradeReq.updateStatus(2);
		
		adminService.saveGrade(updateGrade);
		
		return ResponseEntity.ok(null);
	}
	
	@PreAuthorize("isAuthenticated() and principal.loginId == 'admin'")  // 로그인된 계정의 login_id가 admin일 때만 접근 가능
	@GetMapping("/post/create")
	public String postCreate() {
		log.info("postCreate()");
		
		return "post/create";
	}
	
	@PostMapping("/post/create")
	public String postCreated(@ModelAttribute PostCreateDto dto) {
		log.info("postCreated()");
		log.info("dto={}", dto);
		
		postService.savePost(dto);
		
		return "redirect:/post/notice";
	}
	
	@PostMapping("/post/update/highlight")
	public String postUpdateHighlight(@RequestBody PostUpdateHighlightDto dto) {
		log.info("postUpdateHighlight()");

		postService.updatePostHighlight(dto);
		
		return "redirect:/post/notice";
	}
	
	@PreAuthorize("isAuthenticated() and principal.loginId == 'admin'") // 로그인된 계정의 login_id가 admin일 때만 접근 가능
	@GetMapping("/post/delete")
	public String postDelete(@RequestParam(name = "id") Long id) {
		log.info("postDelete(id={})", id);
		
		postService.deletePostById(id);
		
		return "redirect:/post/notice";
	}
	
	@PreAuthorize("isAuthenticated() and principal.loginId == 'admin'") // 로그인된 계정의 login_id가 admin일 때만 접근 가능
	@GetMapping("/post/update")
	public String postUpdate(Model model, @RequestParam(name = "id") Long id) {
		log.info("postUpdate(id={})", id);
		
		Post post = postService.read(id);
		
		model.addAttribute("post", post);
		
		return "post/update";
	}
	
	@PostMapping("/post/update")
	public String postUpdated(PostUpdateDto dto) {
		log.info("postUpdated(id={})", dto.getId());
		
		postService.updatePost(dto);
		
		return "redirect:/post/notice/details?id=" + dto.getId();
	}
	
//    @ResponseBody
//    @GetMapping("/waitingpayment/read")
//    public ResponseEntity<Page<CoinPaymentWaiting>> readWaitingPayment(
//    	    @RequestParam(name = "page", defaultValue = "0") int page,
//    	    @RequestParam(name = "size", defaultValue = "20") int size) {
//    	
//    	Page<CoinPaymentWaiting> result = adminService.searchAllCoinPaymentWaiting(page, size);
//    	
//    	return ResponseEntity.ok(result);
//    }
    
    @ResponseBody
    @GetMapping("/waitingpayment/read/con")
    public ResponseEntity<Page<CoinPaymentWaiting>> readWaitingPayment(
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "20") int size,
    	    @RequestParam(name = "con", defaultValue = "2") int con) {
    	
    	Page<CoinPaymentWaiting> result;
    	
    	if (con == 2) {
    		result = adminService.searchAllCoinPaymentWaiting(page, size);
    	} else {
    		result = adminService.searchCoinPaymentWaitingByCon(page, size, con);	
    	}
    	
    	return ResponseEntity.ok(result);
    }
    
	@PreAuthorize("isAuthenticated() and principal.loginId == 'admin'") // 로그인된 계정의 login_id가 admin일 때만 접근 가능
	@GetMapping("/chat")
	public String chat() {
		log.info("chat");
		
		return "admin/chat";
	}
	
	@ResponseBody
	@GetMapping("/chat/read/chatroomlist")
	public ResponseEntity<List<ChatRoomWithIsReadDto>> readChatRoomList() {
		List<ChatRoomWithIsReadDto> result = chatService.readChatRoomList();
		
		return ResponseEntity.ok(result);
	}
	
	@ResponseBody
	@GetMapping("chat/read/chatmessage")
	public ResponseEntity<List<ChatMessage>> readChatMessageByChatRoomId(@RequestParam(name = "chatRoomId") Long chatRoomId) {
		List<ChatMessage> result = chatService.readChatMessageByChatRoomId(chatRoomId);
		
		return ResponseEntity.ok(result);
	}

    @ResponseBody
    @GetMapping("/premiumrequest/read/status")
    public ResponseEntity<Page<PremiumRequestDto>> readPremiumWatingList(
    		@RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "20") int size,
    	    @RequestParam(name = "status", defaultValue = "3") int status) {
    	
    	Page<PremiumRequestDto> result;
    	
    	if(status == 3) {
    		result = adminService.searchAllNovelGradeRequests(page, size);
    	} else {
    		result = adminService.searchNovelGradeRequestsByCon(page, size, status);
    	}
		return ResponseEntity.ok(result);
    }
}
