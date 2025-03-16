package com.itwill.lightbooks.web;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.EpisodeCreateDto;
import com.itwill.lightbooks.dto.EpisodeUpdateDto;
import com.itwill.lightbooks.service.EpisodeService;
import com.itwill.lightbooks.service.NovelService;

import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/novel/{novelId}/episode")
public class EpisodeController {
	
	private final EpisodeService epiService;
	private final NovelService novelService;

	@GetMapping("/create")
	public String createEpisode(@PathVariable Long novelId, Model model) {
		log.info("GET createForm(novelId={})", novelId);
		
		Novel novel = novelService.searchById(novelId);
        model.addAttribute("novel", novel);

        EpisodeCreateDto episodeCreateDto = new EpisodeCreateDto();
        episodeCreateDto.setNovelId(novelId);
        
        //초기 카테고리 설정
        if (novel.getGrade() == 0){
            episodeCreateDto.setCategory(1); // novel.grade가 0이면 무료(1)로 설정
        } else {
            episodeCreateDto.setCategory(2); // novel.grade가 1이면 유료(2)로 설정
        }
        model.addAttribute("episodeCreateDto", episodeCreateDto);
        
        //최대 episodeNum 조회(회차 새글 작성시 episodeNum 입력창에서 조건 부여하기 위해)
        Integer maxEpisodeNum = epiService.findMaxEpisodeNumByNovelId(novelId);
        model.addAttribute("maxEpisodeNum", maxEpisodeNum);
     
		model.addAttribute("novelId", novelId);
		
		return "episode/create";
	}
	
	@PostMapping("/create")
	public String createEpisode(@PathVariable Long novelId, EpisodeCreateDto dto,
			RedirectAttributes redirectAttributes, BindingResult bindingResult) {
		log.info("POST create(dto={})", dto);
		
		//TODO 회차num 중복체크 검사
		Novel novel = novelService.searchById(novelId);
		
		
		Episode episode = epiService.createEpisode(novelId, dto);
		log.info("저장된 엔터티 = {}", episode);
		
		redirectAttributes.addAttribute("id", episode.getId());
		redirectAttributes.addAttribute("novelId", episode.getNovel().getId());
		
        return "redirect:/novel/{novelId}/episode/{id}";
	}

	//회차 상세보기
	@GetMapping("/{id}")
    public String episodeDetails(@PathVariable("novelId") Long novelId, @PathVariable("id") Long episodeId, 
    		Model model, HttpSession session) {
        Episode episode = epiService.getEpisodeById(episodeId);
        
        //조회수 증가(로그인 사용자만 조회수 올릴 수 있음, 세션 만료될때까지는 조회수 중복 불가능)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {

            // 세션에서 조회한 에피소드 ID 목록을 가져옴(없으면 새로 생성)
            Set<Long> viewedEpisodes = (Set<Long>) session.getAttribute("viewedEpisodes");
            if (viewedEpisodes == null) {
                viewedEpisodes = new HashSet<>();
                session.setAttribute("viewedEpisodes", viewedEpisodes);
            }

            // 현재 에피소드 ID가 목록에 없으면 조회수 증가 + 목록에 추가
            if (!viewedEpisodes.contains(episodeId)) {
                epiService.increaseViewCount(episodeId);
                viewedEpisodes.add(episodeId);
            }
        }

        // 이전/다음 회차 ID 가져오기
        Long previousEpisodeId = epiService.findPreviousEpisodeId(episode.getNovel().getId(), episode.getEpisodeNum());
        Long nextEpisodeId = epiService.findNextEpisodeId(episode.getNovel().getId(), episode.getEpisodeNum());

        model.addAttribute("episode", episode);
        model.addAttribute("previousEpisodeId", previousEpisodeId); //이전 회차
        model.addAttribute("nextEpisodeId", nextEpisodeId); // 다음 회차
        model.addAttribute("novelId", novelId);

        return "episode/details";
    }
	
	//회차 업데이트
	@GetMapping("/{id}/modify")
	public String modifyEpisode(@PathVariable("novelId") Long novelId, @PathVariable("id") Long episodeId, Model model) {
		log.info("GET modifyEpisode(novelId={}, episodeId={})", novelId, episodeId);
		
	    Episode episode = epiService.getEpisodeById(episodeId);

	    // Episode -> EpisodeUpdateDto 변환
	    EpisodeUpdateDto dto = new EpisodeUpdateDto();
	    dto.setId(episode.getId());
	    dto.setNovelId(episode.getNovel().getId());
	    dto.setTitle(episode.getTitle());
	    dto.setContent(episode.getContent());

	    model.addAttribute("episode", dto);
	    model.addAttribute("novel", episode.getNovel());
	    return "episode/modify";
	}
	
	@PostMapping("/{id}/update")
	public String updateEpisode(@PathVariable("novelId") Long novelId, @PathVariable("id") Long episodeId, 
			@ModelAttribute("episode") EpisodeUpdateDto dto) {
	    log.info("POST updateEpisode(episodeId={}, dto={})", episodeId, dto);
	    
	    dto.setId(episodeId);
        Episode episode = epiService.getEpisodeById(episodeId);
        dto.setNovelId(episode.getNovel().getId());

        epiService.updateEpisode(dto);
        
	    return "redirect:/novel/" + episode.getNovel().getId() + "/episode/" + episode.getId();
	}
	
	@PostMapping("/{id}/delete")
    public String deleteEpisode(@PathVariable("novelId") Long novelId,
                                @PathVariable("id") Long episodeId) {
        log.info("POST deleteEpisode(novelId={}, episodeId={})", novelId, episodeId);
        
		epiService.deleteEpisode(episodeId);

		return "redirect:/novel/" + novelId;

    }
	
}
