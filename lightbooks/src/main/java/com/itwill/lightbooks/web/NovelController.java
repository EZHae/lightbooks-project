package com.itwill.lightbooks.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    
    @PostMapping("/new")
    public String novelCreate(NovelCreateDto dto) {
    	
    	novelService.create(dto);
    	
    	return "redirect:/novel/";
    }
    
}
