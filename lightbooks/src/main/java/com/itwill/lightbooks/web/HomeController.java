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
		
		model.addAttribute("menuId", 157);
	    model.addAttribute("screenId", 1); // 올 라잇 화면과 동일한 처리
	    model.addAttribute("currentURI", request.getRequestURI()); // 추가!
	    
	    loadHomeData(request, model); // 홈 데이터 로딩
		return "home";
	}
	
	// 각 페이지 서브 카테고리의 경로
	@GetMapping("/menu/{menuId}/screen/{screenId}")
	public String menu(@PathVariable Integer menuId, @PathVariable Integer screenId, Model model, HttpServletRequest request) {
	    model.addAttribute("menuId", menuId);
	    model.addAttribute("screenId", screenId);
		
	    if (menuId == 157) {
	        return handleRecommend(screenId, model, request);
	    } else if (menuId == 158) {
	        return handleFree(screenId, model, request);
	    } else if (menuId == 159) {
	        return handlePaid(screenId, model, request);
	    }
	    
		return "error/404";
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
    
	
    // 데이터 case 처리
    // 메인 홈 데이터
	private void loadHomeData(HttpServletRequest request, Model model) {
		model.addAttribute("bestNovels", novelService.getRandemBestNovels(12));
		model.addAttribute("freeNovels", novelService.getNovelsByFreeGrade(6));
		model.addAttribute("paidNovels", novelService.getNovelsByPaidGrade(6));
		model.addAttribute("genreNovelsMap", novelService.getFixedGenreNovels(6));
		model.addAttribute("eventNovels", novelService.getEventNovels(6));
		
		model.addAttribute("requestURI", request.getRequestURI());
		List<Novel> novels = novelService.searchAll();
		model.addAttribute("novels", novels);
	}
	
	// 유료
	private String handlePaid(Integer screenId, Model model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	// 무료
	private String handleFree(Integer screenId, Model model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	// 추천
	private String handleRecommend(Integer screenId, Model model, HttpServletRequest request) {
			switch (screenId) {
			case 1 -> {
				// 올라잇
				loadHomeData(request, model);
				return "home"; // templates/home.html
			}
			case 2 -> {
				// 베스트
				model.addAttribute("novels", novelService.getRecommendedBest());
				return "menu/screen/best";
			}
			case 3 -> {
				// 오늘신작
				model.addAttribute("novels", novelService.getRecommendedNew());
				return"menu/screen/newnovel";
			}
			default -> {
				return "error/404";
			}
		}
	}
}
