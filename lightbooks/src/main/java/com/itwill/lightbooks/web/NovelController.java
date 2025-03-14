package com.itwill.lightbooks.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.NovelCreateDto;
import com.itwill.lightbooks.service.NovelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/novel")
@Slf4j
@RequiredArgsConstructor
public class NovelController {
	
	private final NovelService novelService;
	
    @GetMapping("/new")
    public void novelCreate() {
    	log.info("novelCreate()");
    }
    
    // 작품 생성
    @PostMapping("/new")
    public String novelCreate(@ModelAttribute NovelCreateDto dto, Model model) {
    	Novel novel = novelService.create(dto);

    	model.addAttribute("novel", novel);
    	
    	return "redirect:/";
    }
    
    // 작품 상세보기
    @GetMapping("/{id}")
    public String novelDetail(@PathVariable Integer id, Model model) {
    	log.info("소설 상세정보 페이지: {}",id);
    	
    	Novel novel = novelService.searchById(id);
    	log.info("nove id = {}",novel);
    	
    	model.addAttribute("novel",novel);
    	
    	return "novel/details";
    	
    }
    
    // 작품 수정
    @GetMapping("/modify/{id}")
    public void novelModify(@PathVariable Integer id, Model model) {
    	log.info("작품 수정 페이지()");
    	
    	Novel novel = novelService.searchById(id);
    	
    	model.addAttribute("novel",novel);
    }
    
    @GetMapping("/my-works")
    public String myWorks(Model model) {
    	
    	return "mypage/my-works";
    	
    }
    
    
    
}
