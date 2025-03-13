package com.itwill.lightbooks.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.service.NovelService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

	private final NovelService novelService;
	
	@GetMapping("/")
	public String home(Model model) {
		log.info("home()");
		
		// session.setAttribute("signedInUserId", 1);
		// session.setAttribute("signedInLoginId", "admin");
		// session.setAttribute("signedInNickname", "어드민");
		
		// 그냥 주석달았음: 이지해
		
		List<Novel> novels = novelService.searchAll();
		model.addAttribute("novels", novels);
		
		return "home";
	}
	
}
