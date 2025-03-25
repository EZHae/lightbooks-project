package com.itwill.lightbooks.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.NovelResponseDto;
import com.itwill.lightbooks.dto.PremiumRequestDto;
import com.itwill.lightbooks.service.EpisodeService;
import com.itwill.lightbooks.service.NovelService;
import com.itwill.lightbooks.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

	private final NovelService novelService;
	private final EpisodeService episodeService;
	private final UserService userService;
	
	@GetMapping("/")
	public String home(HttpServletRequest request, Model model) {
		log.info("home()");
		
		// session.setAttribute("signedInUserId", 1);
		// session.setAttribute("signedInLoginId", "admin");
		// session.setAttribute("signedInNickname", "어드민");
		
		// 그냥 주석달았음: 이지해
		
		model.addAttribute("bestNovels", novelService.getRandemBestNovels(12));
		model.addAttribute("freeNovels", novelService.getNovelsByFreeGrade(6));
		model.addAttribute("paidNovels", novelService.getNovelsByPaidGrade(6));
		model.addAttribute("genreNovelsMap", novelService.getFixedGenreNovels(6));
		model.addAttribute("eventNovels", novelService.getEventNovels(6));

		model.addAttribute("requestURI", request.getRequestURI());
		List<Novel> novels = novelService.searchAll();
		model.addAttribute("novels", novels);

		return "home";
	}
	
	@GetMapping("/menu/{menuId}/screen/{screenId}")
	public String menu(@PathVariable Integer menuId) {
		
		return "redirect:/";
	}
	
	@GetMapping("/coinshop")
	public void coinshop() {
		log.info("coinshop()");
		
	}
	
    @GetMapping("/mileageshop")
    public void mileageshop() {
    	log.info("mileageshop");
    }
    
    @GetMapping("/error")
    public void error() {
    	log.info("error_page()");
    }
}
