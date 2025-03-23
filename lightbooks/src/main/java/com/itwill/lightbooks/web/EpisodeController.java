package com.itwill.lightbooks.web;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.EpisodeBuyDto;
import com.itwill.lightbooks.dto.EpisodeCreateDto;
import com.itwill.lightbooks.dto.EpisodeUpdateDto;
import com.itwill.lightbooks.service.BookmarkService;
import com.itwill.lightbooks.service.EpisodeService;
import com.itwill.lightbooks.service.NovelService;
import com.itwill.lightbooks.service.OrderService;
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
	private final OrderService orderService; // COIN_PAYMENT 테이블 생성하기 위함
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
        
        // episodeNum 기본값 설정 (최대값 + 1)
        episodeCreateDto.setEpisodeNum(maxEpisodeNum != null ? maxEpisodeNum + 1 : 1);
     
		model.addAttribute("novelId", novelId);
		
		return "episode/create";
	}
	
	@PostMapping("/create")
	public String createEpisode(@PathVariable Long novelId, EpisodeCreateDto dto,
			RedirectAttributes redirectAttributes) {
		log.info("POST create(dto={})", dto);

		// 현재 최대 episodeNum 조회 및 설정
	    Integer maxEpisodeNum = epiService.findMaxEpisodeNumByNovelId(novelId);
	    
	    // 작성자가 직접 번호를 입력할 수 있도록 하고, 번호를 입력하지 않을 경우 자동으로 차례대로 부여
	    if (dto.getEpisodeNum() == null) {
	        dto.setEpisodeNum(maxEpisodeNum != null ? maxEpisodeNum + 1 : 1);
	    }

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
        
     // 조회수 증가 (로그인 사용자만, 세션 만료 전 중복 불가)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            currentUserId = ((User) authentication.getPrincipal()).getUserId();
            System.out.println("현재 로그인된 사용자 ID: " + currentUserId);

            // 세션에서 조회한 에피소드 ID 목록 관리
            Set<Long> viewedEpisodes = (Set<Long>) session.getAttribute("viewedEpisodes");
            if (viewedEpisodes == null) {
                viewedEpisodes = new HashSet<>();
                session.setAttribute("viewedEpisodes", viewedEpisodes);
            }

            // 현재 에피소드 ID가 목록에 없으면 조회수 증가 및 목록에 추가
            if (!viewedEpisodes.contains(episodeId) && episode.getNovel() != null && !episode.getNovel().getUserId().equals(currentUserId)) {
                epiService.increaseViewCount(episodeId);
                viewedEpisodes.add(episodeId);
            }
        }

        // 작성자 여부 확인
        boolean isOwner = (currentUserId != null) && novelService.isUserOwnerOfNovel(novelId, currentUserId);
        model.addAttribute("isOwner", isOwner);

        // 소설 정보 조회
        Novel novel = novelService.searchById(novelId);
        log.info("novel id = {}", novel);
        model.addAttribute("novel", novel);

        // 이전/다음 회차 ID 조회 (공지 제외)
        Long previousEpisodeId = epiService.findPreviousEpisodeId(episode.getNovel().getId(), episode.getEpisodeNum());
        Long nextEpisodeId = epiService.findNextEpisodeId(episode.getNovel().getId(), episode.getEpisodeNum());

        // 이전/다음 회차 정보 조회
        Episode previousEpisode = (previousEpisodeId != null) ? epiService.getEpisodeById(previousEpisodeId) : null;
        Episode nextEpisode = (nextEpisodeId != null) ? epiService.getEpisodeById(nextEpisodeId) : null;
        model.addAttribute("previousEpisode", previousEpisode);
        model.addAttribute("nextEpisode", nextEpisode);
        
        // 이전/다음 회차 구매 여부 확인
        boolean previousEpisodeIsPurchased = (currentUserId != null) && (previousEpisode != null) && bookmarkService.isPurchasedByUser(currentUserId, novelId, previousEpisodeId);
        boolean nextEpisodeIsPurchased = (currentUserId != null) && (nextEpisode != null) && bookmarkService.isPurchasedByUser(currentUserId, novelId, nextEpisodeId);

        model.addAttribute("episode", episode);
        model.addAttribute("previousEpisodeId", previousEpisodeId);
        model.addAttribute("nextEpisodeId", nextEpisodeId);
        model.addAttribute("novelId", novelId);

        // 이용권 수 조회
        Long globalTicketCount = ticketService.getGlobalTicketCount(authentication);
        Long novelTicketCount = ticketService.getNovelTicketCount(authentication, novelId);
        model.addAttribute("globalTicketCount", globalTicketCount);
        model.addAttribute("novelTicketCount", novelTicketCount);

        // 이전/다음 회차 무료/구매 여부
        model.addAttribute("previousEpisodeIsFree", (previousEpisode != null) && (previousEpisode.getCategory() == 1));
        model.addAttribute("nextEpisodeIsFree", (nextEpisode != null) && (nextEpisode.getCategory() == 1));
        model.addAttribute("previousEpisodeIsPurchased", previousEpisodeIsPurchased);
        model.addAttribute("nextEpisodeIsPurchased", nextEpisodeIsPurchased);

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

	@ResponseBody
	@PostMapping("/buy")
	public ResponseEntity<?> episodeBuy(@RequestBody EpisodeBuyDto dto) {
		
		int result = dto.getType();
		Long ticketId;
		
		if (result == 2) { // 타입이 2인 경우 (코인으로 구매)
			orderService.saveCoinPaymentFromEpisodeBuyDto(dto); // 코인 이용내역 테이블 추가
		} else { // 타입이 0, 1인 경우 (이용권으로 구매)
			ticketId = ticketService.deleteTicketFromEpisodeBuyDto(dto); // 이용권 삭제, 서비스 메서드보면 dto.getType 값에 따라 삭제되는 이용권이 다름
			orderService.saveTicketPaymentFromEpisodeBuyDto(dto, ticketId);
		}
		// 북마크 테이블 추가, 구매하면 북마크에는 무조건 들어가니 if문 밖에 설정
		bookmarkService.saveBookmarkFromEpisodeBuyDto(dto); 
		
		
		return ResponseEntity.ok(null);
	}
	
}
