package com.itwill.lightbooks.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.UserSignUpDto;
import com.itwill.lightbooks.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;

    @GetMapping("/signin")
    public void signin() {
        log.info("GET signIn");
    }
    
    @GetMapping("/signup")
    public void signup() {
        log.info("GET signup");
    }
    
    @PostMapping("/signup")
    public String signup(UserSignUpDto dto) {
    	log.info("POST signup");
    	
    	User user = userService.create(dto);
    	log.info("회원가입 성공: {}", user);
    	
    	return "redirect:/user/signin";
    }
}
