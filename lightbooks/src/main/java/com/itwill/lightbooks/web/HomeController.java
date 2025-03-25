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
       model.addAttribute("mainCategory", "recommend");
       model.addAttribute("currentURI", request.getRequestURI()); // 추가!
       loadHomeData(request, model); // 홈 데이터 로딩
       
      return "home";
   }
   @GetMapping("/free")
   public String freehome(HttpServletRequest request, Model model) {
      log.info("home()");
      
      model.addAttribute("menuId", 158);
       model.addAttribute("screenId", 1); // 올 라잇 화면과 동일한 처리
       model.addAttribute("mainCategory", "free");
       model.addAttribute("currentURI", request.getRequestURI()); // 추가!
       loadHomeData(request, model); // 홈 데이터 로딩
       
      return "freehome";
   }
   @GetMapping("/paid")
   public String paidhome(HttpServletRequest request, Model model) {
      log.info("home()");
      
      model.addAttribute("menuId", 159);
       model.addAttribute("screenId", 1); // 올 라잇 화면과 동일한 처리
       model.addAttribute("mainCategory", "paid");
       model.addAttribute("currentURI", request.getRequestURI()); // 추가!
       loadHomeData(request, model); // 홈 데이터 로딩
       
      return "paidhome";
   }
   
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

   @GetMapping("/free/{menuId}/screen/{screenId}")
   public String freeRouter(@PathVariable Integer menuId,
                            @PathVariable Integer screenId,
                            Model model,
                            HttpServletRequest request) {
       model.addAttribute("menuId", menuId);
       model.addAttribute("screenId", screenId);
       model.addAttribute("mainCategory", "free");
       model.addAttribute("currentURI", request.getRequestURI());

       return handleFree(screenId, model, request);
   }
   
   @GetMapping("/paid/{menuId}/screen/{screenId}")
   public String paidRouter(@PathVariable Integer menuId,
                            @PathVariable Integer screenId,
                            Model model,
                            HttpServletRequest request) {
       model.addAttribute("menuId", menuId);
       model.addAttribute("screenId", screenId);
       model.addAttribute("mainCategory", "paid");
       model.addAttribute("currentURI", request.getRequestURI());

       return handlePaid(screenId, model, request);
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
   }
   // 프리 홈 데이터
   private void loadFreeHomeData(HttpServletRequest request, Model model) {
//      model.addAttribute("bestNovels", novelService.getFreeRecommendNewNovels(12));
//      model.addAttribute("freeNovels", novelService.getFreesPopularSerialNovels(6));
//      model.addAttribute("paidNovels", novelService.getFreePopularCompletedNovels(6));
//      model.addAttribute("genreNovelsMap", novelService.getFreeGenreNovels(6));
//      model.addAttribute("eventNovels", novelService.getFreeEventNovels(6));
      
      model.addAttribute("requestURI", request.getRequestURI());
   }
   
   // 무료
   private String handleFree(Integer screenId, Model model, HttpServletRequest request) {
      switch (screenId) {
         case 1 -> {
            // 자유
            loadFreeHomeData(request, model);
            return "freehome";
         }
         case 2 -> {
            // 베스트
   //         model.addAttribute("novels", novelService.getFreeBest());
            return "free/screen/freebest";
         }
         case 3 -> {
            // 무료 오늘신작
   //         model.addAttribute("novels", novelService.getFreeNew());
            return "free/screen/freenew";
         }
         case 4 -> {
            // 무료 판타지
   //         model.addAttribute("novels", novelService.getFreeByGenre("fantasy"));
            return "free/screen/fantasy";
         }
         case 5 -> {
            // 무료 로판
   //         model.addAttribute("novels", novelService.getFreeByGenre("romance-fantasy"));
            return "free/screen/romancefantasy";
         }
         case 6 -> {
            // 무료 현판
   //         model.addAttribute("novels", novelService.getFreeByGenre("modern-fantasy"));
            return "free/screen/modernfantasy";
         }
         default -> {
            return "error/404";
         }
      }
   }

   // 무료
   private String handlePaid(Integer screenId, Model model, HttpServletRequest request) {
      switch (screenId) {
         case 1 -> {
            // 유료 올라잇
   //         model.addAttribute("novels", novelService.getPaidNovels());
            return "paidhome";
         }
         case 2 -> {
            // 유료 베스트
   //         model.addAttribute("novels", novelService.getPaidBest());
            return "paid/screen/paidbest";
         }
         case 3 -> {
            // 유료 오늘신작
   //         model.addAttribute("novels", novelService.getPaidNew());
            return "paid/screen/paidnew";
         }
         case 4 -> {
            // 유료 판타지
   //         model.addAttribute("novels", novelService.getPaidByGenre("fantasy"));
            return "paid/screen/paidfantasy";
         }
         case 5 -> {
            // 유료 로판
   //         model.addAttribute("novels", novelService.getPaidByGenre("romance-fantasy"));
            return "paid/screen/paidromancefantasy";
         }
         case 6 -> {
            // 유료 현판
   //         model.addAttribute("novels", novelService.getPaidByGenre("modern-fantasy"));
            return "paid/screen/paidmodernfantasy";
         }
         default -> {
            return "error/404";
         }
      }
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
            return "recommend/screen/recommendbest";
         }
         case 3 -> {
            // 오늘신작
            Map<LocalDate, List<Novel>> novelsByDate = novelService.getRecommendedNew();
            model.addAttribute("novels", novelsByDate);
            return"recommend/screen/recommendnew";
         }
         default -> {
            return "error/404";
         }
      }
   }
}


