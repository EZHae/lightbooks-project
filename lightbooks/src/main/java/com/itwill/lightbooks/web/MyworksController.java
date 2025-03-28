package com.itwill.lightbooks.web;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.CoinPayment;
import com.itwill.lightbooks.domain.CoinPaymentIncome;
import com.itwill.lightbooks.dto.NovelResponseDto;
import com.itwill.lightbooks.service.MyworksService;
import com.itwill.lightbooks.service.NovelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/myworks")
public class MyworksController {
	
	private final NovelService novelService;
	private final MyworksService myworksService;

    // 내 작품 페이지
	@PreAuthorize("isAuthenticated() and principal.id == #id")
    @GetMapping("/mynovel")
    public void myWorks(@RequestParam(name = "id") Long id, Model model) {
    	log.info("myWorks()");
    	log.info("현재 접속 아이디: {}", id);
    	
    	// 유저의 소설 목록 가져오기
    	List<NovelResponseDto> novels = novelService.getNovelByUserId(id);
    	log.info("novels : {}", novels);
    	
    	// 해당 유저의 프리미엄 신청 status 확인
    	Map<Long, String> premiumStatus = novelService.getUserPremiumStatus(id);
    	log.info("premiumStatus : {}", premiumStatus);
    	
    	model.addAttribute("novels", novels);
    	model.addAttribute("premiumStatus", premiumStatus);
    }
    
	@PreAuthorize("isAuthenticated() and principal.id == #id")
    @GetMapping("/income")
    public void income(@RequestParam(name = "id") Long id) {
    }
    
	@PreAuthorize("isAuthenticated() and principal.id == #id")
    @GetMapping("/income/details")
    public String donationDetails(@RequestParam(name = "id") Long id, @RequestParam(name= "novelId") Long novelId, Model model) {
    	String novelTitle = novelService.searchById(novelId).getTitle();
    	log.info("수익관리: " + novelTitle);
    	
    	model.addAttribute("novelId", novelId);
    	model.addAttribute("novelTitle", novelTitle);
    	
    	return "/myworks/income-details";
    }
    
    @ResponseBody
    @GetMapping("/income/read")
    public ResponseEntity<Page<Object[]>> readNovelWithIncome(@RequestParam(name = "userId") Long userId,
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "5") int size) {
    	log.info("controller::page={}, size={}", page, size);
    	Page<Object[]> result = myworksService.readNovelWithIncome(userId, page, size);
    	
    	return ResponseEntity.ok(result);
    }
    
    @ResponseBody
    @GetMapping("/income/details/read")
    public ResponseEntity<Page<CoinPaymentIncome>> readIncome(@RequestParam(name = "novelId") Long novelId,
    	    @RequestParam(name = "page", defaultValue = "0") int page,
    	    @RequestParam(name = "size", defaultValue = "10") int size,
    	    @RequestParam(name = "type", defaultValue = "4") int type,
    	    Model model) {
    	
    	Page<CoinPaymentIncome> result = myworksService.readIncome(novelId, page, size, type);
    	return ResponseEntity.ok(result);
    }
}
