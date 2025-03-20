package com.itwill.lightbooks.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.EpisodeCreateDto;
import com.itwill.lightbooks.dto.EpisodeUpdateDto;
import com.itwill.lightbooks.service.BookmarkService;
import com.itwill.lightbooks.service.EpisodeService;
import com.itwill.lightbooks.service.NovelService;
import com.itwill.lightbooks.service.TicketService;
import com.itwill.lightbooks.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/novel/{novelId}/episode")
public class EpisodeController {
	
	private final EpisodeService epiService;
	private final NovelService novelService;
	private final BookmarkService bookmarkService;
	private final UserService userService;
	private final TicketService ticketService;

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
			RedirectAttributes redirectAttributes) {
		log.info("POST create(dto={})", dto);
		
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
        	
        	// 현재 로그인한 사용자 정보 가져오기
            Object principal = authentication.getPrincipal();
            Long currentUserId = null;
            if (principal instanceof User) {
                currentUserId = ((User) principal).getUserId();
                System.out.println("현재 로그인된 사용자 pk(id): " + currentUserId);
            }

            // 세션에서 조회한 에피소드 ID 목록을 가져옴(없으면 새로 생성)
            Set<Long> viewedEpisodes = (Set<Long>) session.getAttribute("viewedEpisodes");
            if (viewedEpisodes == null) {
                viewedEpisodes = new HashSet<>();
                session.setAttribute("viewedEpisodes", viewedEpisodes);
            }

            // 현재 에피소드 ID가 목록에 없으면 조회수 증가 + 목록에 추가
            if (!viewedEpisodes.contains(episodeId) && episode.getNovel() != null
                    && !episode.getNovel().getUserId().equals(currentUserId)) {
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
	
	//ResponseEntity
	@GetMapping("/api/check-episode-num")
    public ResponseEntity<Boolean> checkEpisodeNum(@RequestParam Long novelId, @RequestParam int episodeNum) {
        boolean exists = epiService.doesEpisodeNumExist(novelId, episodeNum);
        return ResponseEntity.ok(exists);
    }
	
	// 유료회차 구매 여부 확인
	@GetMapping("/{episodeId}/check")
	@ResponseBody
	public ResponseEntity<String> checkEpisode(@PathVariable Long novelId, @PathVariable Long episodeId) {
		// 인증 정보 확인
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
		    return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}

	    // 현재 로그인된 사용자 ID 가져오기
	    Long currentUserId = ((User) auth.getPrincipal()).getUserId();
	    log.info("현재 로그인된 사용자 ID: " + currentUserId);

	    try {
	        Episode episode = epiService.getEpisodeById(episodeId);

	        // 무료 회차 확인
	        if (episode.getCategory() == 1) {
	            return ResponseEntity.ok("FREE");
	        }

	        // 유료 회차 구매 여부 확인
	        boolean purchased = bookmarkService.isPurchasedByUser(currentUserId, novelId, episodeId);
	        log.info("유저 ID: " + currentUserId);
	        log.info("소설 ID: " + novelId);
	        log.info("에피소드 ID: " + episodeId);
	        log.info("구매 여부: " + purchased);

	        if (purchased) {
	        	log.info("구매된 회차");
	            return ResponseEntity.ok("PURCHASED");
	        }

	        // 구매되지 않은 상태
	        log.info("구매하지 않은 유료 회차");
	        return ResponseEntity.ok("NOT_PURCHASED");
	    } catch (EntityNotFoundException e) {
	        return ResponseEntity.status(404).body("해당 회차를 찾을 수 없습니다.");
	    }
	}

}
