package com.itwill.lightbooks.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
import com.itwill.lightbooks.dto.NovelListItemDto;
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

	public static final int STATE_COMPLETE = 0; // 완결
	public static final int STATE_SERIAL = 1; // 연재중
	public static final int GRADE_FREE = 0; // 무료
	public static final int GRADE_PAID = 1; // 유료
	private final NovelService novelService;
	private final EpisodeService episodeService;
	private final UserService userService;
	
	@GetMapping("/")
	public String home(HttpServletRequest request, Model model) {
		log.info("home()");
		
		model.addAttribute("menuId", 157);
	    model.addAttribute("screenId", 1); // 올 라잇 화면과 동일한 처리
	    model.addAttribute("mainCategory", "recommend");
	    model.addAttribute("currentURI", request.getRequestURI()); // 추가!
	    
		return "home";
	}
	
	// 무료 페이지
	@GetMapping("/free")
	public String freehome(HttpServletRequest request, Model model) {
		log.info("freehome()");
		
		model.addAttribute("menuId", 158);
	    model.addAttribute("screenId", 1); // 올 라잇 화면과 동일한 처리
	    model.addAttribute("mainCategory", "free");
	    model.addAttribute("currentURI", request.getRequestURI()); // 추가!
	    
		return "freehome";
	}
	// 유료 페이지
	@GetMapping("/paid")
	public String paidhome(HttpServletRequest request, Model model) {
		log.info("paidhome()");
		
		model.addAttribute("menuId", 159);
	    model.addAttribute("screenId", 1); // 올 라잇 화면과 동일한 처리
	    model.addAttribute("mainCategory", "paid");
	    model.addAttribute("currentURI", request.getRequestURI()); // 추가!
	    
		return "paidhome";
	}
	
	// 추천 페이지의 서브카테고리
	@GetMapping("/recommend/{menuId}/screen/{screenId}")
	public String recommendRouter(@PathVariable Integer menuId,
	                              @PathVariable Integer screenId,
	                              Model model,
	                              HttpServletRequest request) {
	    model.addAttribute("menuId", menuId);
	    model.addAttribute("screenId", screenId);
	    model.addAttribute("mainCategory", "recommend");
	    model.addAttribute("currentURI", request.getRequestURI());

       return handleRecommend(screenId, model, request);
   }
   // 유료/무료 페이지의 서브카테고리
   @GetMapping("/{type}/{menuId}/screen/{screenId}")
   public String handleScreenRoute(@PathVariable String type,
		   						@PathVariable Integer menuId,
		   						@PathVariable Integer screenId,
                            Model model,
                            HttpServletRequest request) {
       model.addAttribute("menuId", menuId);
       model.addAttribute("screenId", screenId);
       model.addAttribute("mainCategory", type);
       model.addAttribute("currentURI", request.getRequestURI());

       return handleScreen(type,screenId, model, request);
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
    
    @GetMapping("/error/403")
    public void error403() {
    	
    }
    
   
    // ======================================== 데이터 비동기 / case 처리 ========================================
    // 비동기 api 작성
    @ResponseBody
    @GetMapping("/api/recommend/home")
    public ResponseEntity<?> recommendHome() {
    	
 	   Map<String, Object> data = Map.of(
         "bestNovels", novelService.getRandemBestNovels(12),
         "freeNovels", novelService.getNovelsByFreeGrade(6),
         "paidNovels", novelService.getNovelsByPaidGrade(6),
         "genreNovelsMap", novelService.getFixedGenreNovels(6),
         "eventNovels", novelService.getEventNovels(6)
         );
 	   
 	   
 	   return ResponseEntity.ok(data);
    }
    
    @ResponseBody
    @GetMapping("/api/recommend/best")
    public ResponseEntity<?> recommendBest(@RequestParam(defaultValue = "100") int limit) {
 	   return ResponseEntity.ok(novelService.getRecommendedBest(limit));
    }
    
    @ResponseBody
    @GetMapping("/api/recommend/new")
    public ResponseEntity<?> recommendNew() {
 	   return ResponseEntity.ok(novelService.getRecommendedNew());
    }
    
 // 무료소설 (free)
    @ResponseBody
    @GetMapping("/api/free/home")
    public ResponseEntity<?> freeHome() {
        Map<String, Object> data = Map.of(
            "bestNovels", novelService.getRecommendNewNovels(GRADE_FREE, 12),
            "freeNovels", novelService.getPopularSerialNovels(GRADE_FREE, STATE_SERIAL, 6),
            "paidNovels", novelService.getPopularCompletedNovels(GRADE_FREE, STATE_COMPLETE, 6),
            "genreNovelsMap", novelService.getGenreNovels(GRADE_FREE, 6),
            "eventNovels", novelService.getEventNovels(GRADE_FREE, 6)
        );
        return ResponseEntity.ok(data);
    }
    @ResponseBody
    @GetMapping("/api/free/best")
    public ResponseEntity<?> freeBest(@RequestParam(defaultValue = "100") int limit) {
 	   return ResponseEntity.ok(novelService.getRecommendedFreePaidBest(GRADE_FREE, limit));
    }
    @ResponseBody
    @GetMapping("/api/free/new")
    public ResponseEntity<?> freeNew() {
 	   return ResponseEntity.ok(novelService.getRecommendedNew(GRADE_FREE));
    }
    // 유료
    @ResponseBody
    @GetMapping("/api/paid/home")
    public ResponseEntity<?> paidHome() {
        Map<String, Object> data = Map.of(
            "bestNovels", novelService.getRecommendNewNovels(GRADE_PAID, 12),
            "freeNovels", novelService.getPopularSerialNovels(GRADE_PAID, STATE_SERIAL, 6),
            "paidNovels", novelService.getPopularCompletedNovels(GRADE_PAID, STATE_COMPLETE, 6),
            "genreNovelsMap", novelService.getGenreNovels(GRADE_PAID, 6),
            "eventNovels", novelService.getEventNovels(GRADE_PAID, 6)
        );
        return ResponseEntity.ok(data);
    }
    @ResponseBody
    @GetMapping("/api/paid/best")
    public ResponseEntity<?> paidBest(@RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(novelService.getRecommendedFreePaidBest(GRADE_PAID, limit));
    }
    @ResponseBody
    @GetMapping("/api/paid/new")
    public ResponseEntity<?> paidNew() {
        return ResponseEntity.ok(novelService.getRecommendedNew(GRADE_PAID));
    }
    
    // 장르 api 통합
    @ResponseBody
    @GetMapping("/api/{type}/genre/{genreName}")
    public ResponseEntity<?> novelsByGenre(@PathVariable String type,
                                           @PathVariable String genreName) {
        int grade = type.equals("free") ? GRADE_FREE : GRADE_PAID;
        return ResponseEntity.ok(novelService.getNovelsByGenre(grade, genreName, 50));
    }
    
	//케이스
	private String handleScreen(String type, Integer screenId, Model model, HttpServletRequest request) {
		int grade = type.equals("free") ? 0 : 1;
		String basePath = type + "/screen/";
		switch (screenId) {
			case 1 -> {
				// 자유
				return type + "home";
			}
			case 2 -> {
				// 베스트
				return basePath + type + "best";
			}
			case 3 -> {
				// 무료 오늘신작
				return basePath + type + "new";
			}
			case 4 -> {
				// 무료 판타지
				return basePath + "fantasy";
			}
			case 5 -> {
				// 무료 로맨스
				return basePath + "romance";
			}
			case 6 -> {
				// 무료 무협
				return basePath +"martialarts";
			}
			case 7 -> {
				// 무료 로판
				return basePath +"romancefantasy";
			}
			case 8 -> {
				// 무료 현판
				return basePath +"modernfantasy";
			}
			case 9 -> {
				// 무료 드라마
				return basePath +"drama";
			}
			default -> {
				return "error/404";
			}
		}
	}
    //추천 홈 케이스
   private String handleRecommend(Integer screenId, Model model, HttpServletRequest request) {
         switch (screenId) {
         case 1 -> {
            // 올라잇
            return "home"; // templates/home.html
         }
         case 2 -> {
            // 베스트
            return "recommend/screen/recommendbest";
         }
         case 3 -> {
            // 오늘신작
            return"recommend/screen/recommendnew";
         }
         default -> {
            return "error/404";
         }
      }
   }
}
