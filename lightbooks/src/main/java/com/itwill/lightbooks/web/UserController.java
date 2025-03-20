package com.itwill.lightbooks.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
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
import com.itwill.lightbooks.domain.MileagePayment;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.domain.UserWallet;
import com.itwill.lightbooks.dto.PaymentRequestDto;
import com.itwill.lightbooks.dto.UserSignUpDto;
import com.itwill.lightbooks.dto.UserUpdatePasswordDto;
import com.itwill.lightbooks.dto.UserUpdateProfileDto;
import com.itwill.lightbooks.service.OrderService;
import com.itwill.lightbooks.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;
	private final OrderService orderService;
    
    @GetMapping("/signup")
    public void signup() {
        log.info("GET signup");
    }
    
    @PostMapping("/signup")
    public String signup(UserSignUpDto dto) {
    	log.info("POST signup");
    	
    	User user = userService.create(dto);
    	log.info("회원가입 성공: {}", user);
    	
    	return "redirect:/";
    }
    
    @GetMapping("/details")
    public void details(@RequestParam(name = "id") Long id, Model model) {
    	log.info("GET details");
    	
    	User user = userService.searchById(id);
    	
    	model.addAttribute("user", user);
    }
    
    @PostMapping("/updateProfile")
    public String updateProfile(UserUpdateProfileDto dto) {
    	log.info("POST updateProfile");
    	
    	userService.updateProfile(dto);
    	
    	return "redirect:/user/signout";
    }
    
    @PostMapping("/updatePassword")
    public String updatePassword(UserUpdatePasswordDto dto) {
    	
    	userService.updatePassword(dto);
    	
    	return "redirect:/user/signout";
    }
    
    @PostMapping("/delete")
    public String deleteById(@RequestParam(name = "id") Long id) {
    	
    	userService.deleteUser(id);
    	
    	return "redirect:/user/signout";
    }
    
    @GetMapping("/coinpayment")
    public String coinPayment(@RequestParam(name = "id") Long id) {
    	log.info("coinPayment(id={})", id);
    	
    	return "/user/coin-payment";
    }
    
    @GetMapping("/mileagepayment")
    public String mileagePayment(@RequestParam(name = "id") Long id) {
    	log.info("mileagePayment(id={})", id);
    	
    	return "/user/mileage-payment";
    }
    
    @GetMapping("/bookmark")
    public void bookmark(@RequestParam(name = "id") Long id) {
    	log.info("bookmark(id={})", id);
    	
    }
    
    /* ResponseBody */
    @ResponseBody
    @GetMapping("/checkLoginId")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam(name = "loginId") String loginId) {
    	log.info("check?loginId()");
    	
    	User user = userService.searchByLoginId(loginId);
    	log.info("searchByLoginId(loginId={})", loginId);
    	
    	Boolean result = (user == null) ? false : true;
    	log.info("reuslt={}", result);
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam(name = "nickname") String nickname) {
    	log.info("check?nickname()");
    	
    	User user = userService.searchByNickname(nickname);
    	log.info("searchByNickname(nickname={})", nickname);
    	
    	Boolean result = (user == null) ? false : true;
    	log.info("reuslt={}", result);
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/checkPhonenumber")
    public ResponseEntity<Boolean> checkPhonenumber(@RequestParam(name = "phonenumber") String phonenumber) {
    	log.info("check?phonenumber()");
    	
    	User user = userService.searchByPhonenumber(phonenumber);
    	log.info("searchByPhonenumber(phonenumber={})", phonenumber);
    	
    	Boolean result = (user == null) ? false : true;
    	log.info("reuslt={}", result);
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam(name = "email") String email) {
    	log.info("check?email()");
    	
    	User user = userService.searchByEmail(email);
    	log.info("searchByEmail(email={})", email);
    	
    	Boolean result = (user == null) ? false : true;
    	log.info("reuslt={}", result);
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @PostMapping("/checkOldPassword")
    public ResponseEntity<Boolean> checkPassword(@RequestBody UserUpdatePasswordDto dto) {
    	log.info("{}", dto);
    	
    	Boolean result = userService.checkPassword(dto);
    	
    	return ResponseEntity.ok(result);
    }
    
    /*
    @ResponseBody
    @GetMapping("/getUserWallet")
    public ResponseEntity<Long> getUserWallet(@RequestParam(name = "userId") Long userId,
    												@RequestParam(name = "type") String type) {
    	log.info("getUserWallet?userId");
    	log.info("userId={}", userId);
    	
    	UserWallet userWallet = userService.SearchUserWalletByUserId(userId);
    	log.info("userWallet={}", userWallet);
    	
    	if (type.equals("c")) {
    		return ResponseEntity.ok(userWallet.getCoin());
    	} else {
    		return ResponseEntity.ok(userWallet.getMileage());
    	}
    }
    */
    
    @ResponseBody
    @GetMapping("/getUserWallets")
    public ResponseEntity<UserWallet> getUserWallets(@RequestParam(name = "userId") Long userId) {
    	log.info("getUserWallet?userId");
    	log.info("userId={}", userId);
    	
    	UserWallet userWallet = userService.SearchUserWalletByUserId(userId);
    	log.info("잉userWallet={}", userWallet);
    	
    	return ResponseEntity.ok(userWallet);
    }
    
    // Servlet 또는 Controller에서 세션에서 errorMessage를 제거하는 코드
    @ResponseBody
    @PostMapping("/removeSession")
    public ResponseEntity<String> clearErrorMessage(HttpSession session) {
        session.removeAttribute("errorMessage");
        return ResponseEntity.ok("ok");
    }
    
    @ResponseBody
    @GetMapping("/coinpayment/read")
    public ResponseEntity<Page<CoinPayment>> readCoinPayment(@RequestParam(name = "userId") Long userId,
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "5") int size,
    	    @RequestParam(name = "type", defaultValue = "0") int type) {
    	
    	Page<CoinPayment> result = orderService.readCoinPaymentByUserId(userId, page, size, type);
    	log.info("이거보세요.{}", result);
    	
        // 페이징 정보와 데이터 함께 전달
//        Map<String, Object> response = new HashMap<>();
//        response.put("data", result.getContent());
//        response.put("totalItems", result.getTotalElements());
//        response.put("totalPages", result.getTotalPages());
//        response.put("currentPage", result.getNumber());
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/coinpaymentwaiting/read")
    public ResponseEntity<Page<CoinPaymentWaiting>> readCoinPaymentWaiting(@RequestParam(name = "userId") Long userId,
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "5") int size,
    	    @RequestParam(name = "type", defaultValue = "0") int type) {
    	
    	Page<CoinPaymentWaiting> result = orderService.readCoinPaymentWaitingByUserId(userId, page, size, type);
    	log.info("waiting.{}", result);
    	
        // 페이징 정보와 데이터 함께 전달
//        Map<String, Object> response = new HashMap<>();
//        response.put("data", result.getContent());
//        response.put("totalItems", result.getTotalElements());
//        response.put("totalPages", result.getTotalPages());
//        response.put("currentPage", result.getNumber());
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @PostMapping("/coinpayment/reApp")
    public ResponseEntity<String> coinPaymentReApp(@RequestBody PaymentRequestDto dto) {
    	log.info("coinPaymentReApp()");
    	User user = userService.searchById(dto.getUserId());  	
    	
    	CoinPayment coinpayment = CoinPayment.builder().userId(dto.getUserId()).coin(dto.getCoin()).cash(dto.getCash()).type(dto.getType()).build();
    	CoinPaymentWaiting coinpaymentWaiting = CoinPaymentWaiting.builder().user(user).coin(dto.getCoin()).cash(dto.getCash()).type(dto.getType()).con(0).build();
    	
    	orderService.saveCoinPayment(coinpayment);
    	orderService.saveCoinPaymentWaiting(coinpaymentWaiting);
    	
    	return ResponseEntity.ok("/");
    }

    @ResponseBody
    @GetMapping("/mileagepayment/read")
    public ResponseEntity<Page<MileagePayment>> readMileagePayment(@RequestParam(name = "userId") Long userId,
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "5") int size,
    	    @RequestParam(name = "type", defaultValue = "0") int type) {
    	
    	Page<MileagePayment> result = userService.readMileagePaymentByUserId(userId, page, size, type);

    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @PostMapping("/mileagepayment/app")
    public ResponseEntity<String> mileagepaymentApp(@RequestBody PaymentRequestDto dto) {
    	log.info("mileagepaymentApp()");
    	
    	userService.saveMileagePaymentWithGlobalTicket(dto);
    	
    	return ResponseEntity.ok("/");
    }
}
