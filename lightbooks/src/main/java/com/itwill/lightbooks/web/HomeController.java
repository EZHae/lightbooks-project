package com.itwill.lightbooks.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {

	@GetMapping("/")
	public String home(HttpSession session) {
		log.info("home()");
		
		session.setAttribute("signedInUserId", 1);
		session.setAttribute("signedInLoginId", "admin");
		session.setAttribute("signedInNickname", "어드민");
		
		// 그냥 주석달았음: 이지해
		
		return "home";
	}
}
