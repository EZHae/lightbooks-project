package com.itwill.lightbooks.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.domain.UserWallet;
import com.itwill.lightbooks.dto.UserSignUpDto;
import com.itwill.lightbooks.dto.UserUpdatePasswordDto;
import com.itwill.lightbooks.dto.UserUpdateProfileDto;
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
    
    // Servlet 또는 Controller에서 세션에서 errorMessage를 제거하는 코드
    @ResponseBody
    @PostMapping("/removeSession")
    public ResponseEntity<String> clearErrorMessage(HttpSession session) {
        session.removeAttribute("errorMessage");
        return ResponseEntity.ok("ok");
    }
}
