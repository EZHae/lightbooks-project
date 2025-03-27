package com.itwill.lightbooks.web;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.lightbooks.dto.NovelResponseDto;
import com.itwill.lightbooks.service.NovelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/myworks")
public class MyworksConroller {
	
	private final NovelService novelService;

    // 내 작품 페이지
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
    
    @GetMapping("/donation")
    public void donation(@RequestParam(name = "id") Long id) {
    	
    }
    
    @GetMapping("/donation-details")
    public String donationDetails(@RequestParam(name = "id") Long id, @RequestParam(name= "novelId") Long novelId) {
    	
    	
    	return "/myworks/donation-details";
    }
}
