package com.itwill.lightbooks.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.EpisodeCreateDto;
import com.itwill.lightbooks.service.EpisodeService;
import com.itwill.lightbooks.service.NovelService;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/novel/{novelId}/episode")
public class EpisodeController {
	
	private final EpisodeService epiService;

	@GetMapping("/create")
	public String create(@PathVariable Integer novelId, Model model) {
		log.info("GET createForm(novelId={})", novelId);

        model.addAttribute("episodeCreateDto", new EpisodeCreateDto());
		model.addAttribute("novelId", novelId);
		
		return "episode/create";
	}
	
	@PostMapping("/create")
	public String create(@PathVariable Long novelId,  EpisodeCreateDto dto) {
		log.info("POST create(dto={})", dto);
		
		Episode episode = epiService.createEpisode(novelId, dto);
		log.info("저장된 엔터티 = {}", episode);
		
		return "redirect:/episode/details";
	}
}
