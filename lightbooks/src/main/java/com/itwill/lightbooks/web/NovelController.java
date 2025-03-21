package com.itwill.lightbooks.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.EpisodeListDto;
import com.itwill.lightbooks.dto.NovelCreateDto;
import com.itwill.lightbooks.dto.NovelItemDto;
import com.itwill.lightbooks.dto.NovelListItemDto;
import com.itwill.lightbooks.dto.NovelResponseDto;
import com.itwill.lightbooks.dto.NovelSearchDto;
import com.itwill.lightbooks.dto.NovelUpdateDto;
import com.itwill.lightbooks.dto.PremiumRequestDto;
import com.itwill.lightbooks.service.BookmarkService;
import com.itwill.lightbooks.service.EpisodeService;
import com.itwill.lightbooks.service.NovelRatingService;
import com.itwill.lightbooks.service.NovelService;
import com.itwill.lightbooks.service.TicketService;
import com.itwill.lightbooks.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/novel")
@Slf4j
@RequiredArgsConstructor
public class NovelController {
	
	private final NovelService novelService;
	private final EpisodeService episodeService; //추가
	private final NovelRatingService novelRatingService;
	private final BookmarkService bookmarkService;
	private final UserService userService;
	private final TicketService ticketService;//추가
	
	
    @GetMapping("/new")
    public void novelCreate() {
    	log.info("소설 생성 페이지");
    }
    
    // 작품 생성
    @PostMapping("/new")
    public String novelCreate(@ModelAttribute NovelCreateDto dto, Model model) {
    	Novel novel = novelService.create(dto);
    	model.addAttribute("novel", novel);
    	
    	return "redirect:/";
    }
    
    // 작품 상세보기
    @GetMapping({"/{id}", "/{id}/episodes"})
    public String novelDetail(@PathVariable Long id, Model model,
          @RequestParam(name = "category", required = false) Integer category,
          @RequestParam(name = "sort", defaultValue = "episodeNum,asc") String sortStr,
            @RequestParam(name = "page", defaultValue = "0") int pageNo,
            HttpServletRequest request) {
       log.info("소설 상세정보 페이지: {}",id);
       
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();

       if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
           throw new IllegalStateException("로그인이 필요합니다."); // 로그인되지 않은 사용자 처리
       }

       // 현재 로그인된 사용자 ID 가져오기
       User currentUser = (User) auth.getPrincipal(); // User 엔터티로 캐스팅
       Long currentUserId = currentUser.getUserId();

       // 작성자 여부 확인
       boolean isOwner = novelService.isUserOwnerOfNovel(id, currentUserId);
       model.addAttribute("isOwner", isOwner); // 작성자 여부를 모델에 추가

       
       Novel novel = novelService.searchById(id);
       log.info("nove id = {}",novel);
       
       model.addAttribute("novel",novel);
//       log.info("novelCheck={}", novel);
       
       // 정렬 객체 생성 (기본값: episodeNum 오름차순) + category가 0(공지)이면 createdTime 내림차순
       Sort sort = Sort.by(Sort.Direction.ASC, "episodeNum"); //기본 정렬
        if(category != null && category == 0){
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        if (sortStr != null && !sortStr.isEmpty()) {
            String[] sortInfo = sortStr.split(","); // "episodeNum,desc" -> ["episodeNum", "desc"]
            if(sortInfo.length == 2){
                Sort.Direction direction = "desc".equalsIgnoreCase(sortInfo[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
                if(category != null && category == 0){
                    sort = Sort.by(direction, "createdTime"); //정렬 객체 생성
                } else {
                    sort = Sort.by(direction, sortInfo[0]);
                }
            }
        }

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(pageNo, 5, sort);

        // EpisodeService를 사용하여 에피소드 목록 가져오기 (페이징, 정렬, 카테고리 필터링)
        Page<EpisodeListDto> episodes = episodeService.getEpisodesByNovelAndCategory(id, category, sortStr.toString(), pageable);

        // "첫 화 보기" 버튼에 사용할 첫 번째 에피소드의 ID 찾기
        Long firstEpisodeId = episodeService.findFirstEpisodeByNovel(id).map(Episode::getId).orElse(null);

        model.addAttribute("episodes", episodes);     // 에피소드 목록 (Page<EpisodeListDto>)
        model.addAttribute("category", category);    // 선택된 카테고리 (null 또는 값)
        model.addAttribute("sort", sortStr.toString()); // 현재 정렬 방식 (문자열)
        model.addAttribute("firstEpisodeId", firstEpisodeId); // 첫 번째 에피소드의 ID
        model.addAttribute("novelId", id);
    
        // 이용권 수 조회
        Long globalTicketCount = ticketService.getGlobalTicketCount(auth);
        Long novelTicketCount = ticketService.getNovelTicketCount(auth, id);

        model.addAttribute("globalTicketCount", globalTicketCount);
        model.addAttribute("novelTicketCount", novelTicketCount);
        
        
        // Ajax 요청인지 확인
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "episode/listOfNovelDetails :: episodeListOfNovelDetails";
        }
        
       return "novel/details";
    }

    
    // 내 작품 페이지
    @GetMapping("/my-works")
    public String myWorks(@RequestParam(name = "id") Long id, Model model) {
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
    	
    	return "mypage/my-works";
    	
    }
    
    // 작품 수정 페이지
    @GetMapping("/modify/{id}")
    public String novelModify(@PathVariable Long id, Model model) {
    	log.info("작품 수정 페이지()");
    	
    	Novel novel = novelService.searchByIdWithGenre(id);
    	log.info("불러온 작품의 장르 ID: " + novel.getNovelGenre());
    	
    	NGenre novelGenre = novel.getNovelGenre().isEmpty() ? null : novel.getNovelGenre().get(0);
    	
    	
    	model.addAttribute("novel",novel);
    	model.addAttribute("novelGenre", novelGenre);
    	return "novel/modify";
    }    
    
    // 작품 삭제
    @PostMapping("/delete")
    public String novelDeleteById(@RequestParam(name = "id") Long id,
    							@RequestParam(name= "userId") Long userId) {
    	log.info("delete novel id : {}", id);
    	
    	novelService.deleteById(id);
    		
    	return "redirect:/novel/my-works?id=" + userId;
    }
    
    // 작품 업데이트
    @PostMapping("/update")
    public String novelUpdateById(NovelUpdateDto dto) {
    	novelService.updateNovel(dto);
    	log.info("작품 업데이트 : {}", dto);
    	
    	return "redirect:/novel/" + dto.getId();
    }
    
    // 작품 검색 페이지
    @GetMapping("/search/result")
    public String novelSearch(@RequestParam(name="keyword", required=false) String keyword,NovelSearchDto dto, Model model) {
    	
    	dto.setKeyword(keyword);
    	dto.setCategory("tw");
    	
    	Page<NovelListItemDto> page = novelService.search(dto, Sort.by("id").descending());
    	model.addAttribute("page", page);
    	
    	// pagination 프래그먼트의 링크(버튼)의 요청 주소로 사용할 문자열을 모델 속성으로 저장.
        model.addAttribute("baseUrl", "/search/result");
        model.addAttribute("keyword", dto.getKeyword());
    	
    	return "novel/search";
    }
    
    // 작품 좋아요 토클 (추가/취소)
    @PostMapping("/{novelId}/like")
    @ResponseBody
	public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable(name = "novelId") Long novelId, 
											@RequestParam(name = "userId") Long userId) {
    	log.info("소설 아이디 : {}, 유저 아이디 : {}", novelId, userId);
    	boolean isLiked = bookmarkService.toggleLike(userId, novelId);
    	int likeCount = bookmarkService.getLikeCount(novelId);
    	
    	Map<String, Object> response = new HashMap<>();
    	response.put("liked", isLiked);
    	response.put("likeCount", likeCount);
    	
    	return ResponseEntity.ok(response);
	}
	
	// 좋아요 개수 조회
	@GetMapping("/{novelId}/like/count")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getLikeCount(@PathVariable(name = "novelId") Long novelId, @RequestParam(name = "userId") Long userId) {
		Integer count = bookmarkService.getLikeCount(novelId);
		boolean liked = bookmarkService.isLiked(userId, novelId); // 사용자가 좋아요 했는지 확인
		
	    Map<String, Object> response = Map.of(
            "liked", liked,
            "likeCount", count
        );
		
		return ResponseEntity.ok(response);
	}
	
	// 사용자가 좋아요 했는지 확인
	@GetMapping
	@ResponseBody
	public ResponseEntity<Boolean> isLiked(@RequestParam Long userId, @PathVariable Long novelId) {
		boolean liked = bookmarkService.isLiked(userId, novelId);
		return ResponseEntity.ok(liked);
	}
	
	
	/**
	 *  해당 작품의 사용자가 유료/무료 신청을 할 때
	 *  - novelId, userId, type(0: 무료, 1: 유료), status(0: 대기, 1: 승인, 2:거절)
	 */
	
    @GetMapping("/premium/{novelId}")
	public String premium(@PathVariable(name = "novelId") Long novelId, Model model) {
		log.info("premium()");
		
		// 해당 유저의 소설이 신청을 한 소설인지
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

       if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
           throw new IllegalStateException("로그인이 필요합니다."); // 로그인되지 않은 사용자 처리
       }

       // 현재 로그인된 사용자 ID 가져오기
       User currentUser = (User) auth.getPrincipal(); // User 엔터티로 캐스팅
       Long currentUserId = currentUser.getUserId();
		
       // 해당 유저의 소설이 신청을 한 소설인지
       Optional<NovelGradeRequest> requstOut = novelService.getPremiumUserIdAndNovelId(currentUserId ,novelId);
		if(requstOut.isPresent()) {
			NovelGradeRequest request = requstOut.get();
			if(request.getStatus() == 0 || request.getStatus() == 1) {
				return "redirect:/error/404"; // 임의로 접근 했을 시 페이지 이동
			}
		}
		
		// 소설 정보
		Novel novel = novelService.searchById(novelId);
    	log.info("novels : {}", novel);
    	Long count = episodeService.getEpisodeCountByNovelId(novelId);
    	log.info("count : {}", count);
    	boolean canApply = novelService.canApplyForPremiun(novelId);
    	
    	model.addAttribute("novel", novel);
    	model.addAttribute("count", count);
    	model.addAttribute("canApply", canApply);
    	
		return "/novel/premium";
	}
    
    // 해당 소설이 신청 조건을 만족 하는 지 확인
    @ResponseBody
    @GetMapping("/premium/can-apply")
    public ResponseEntity<Boolean> canApplyForPremiun(@RequestParam Long novelId){
    	boolean canApply = novelService.canApplyForPremiun(novelId);
    	return ResponseEntity.ok(canApply);
    }
    
    // 사용자가 신청조건에 만족하면 admin의 신청 내역 테이블에 저장
    @ResponseBody
	@PostMapping("/premium/apply")
	public ResponseEntity<NovelGradeRequest> submitPremiumRequest(@RequestBody PremiumRequestDto dto) {
		User user = userService.searchById(dto.getUserId());
		Novel novel = novelService.searchById(dto.getNovelId());
		
		NovelGradeRequest ngReq = NovelGradeRequest.builder().novel(novel).user(user).type(dto.getType()).status(dto.getStatus()).build();
		log.info("NovelGradeRequest : {}", ngReq);
		novelService.saveGradeRequest(ngReq);
		
		return ResponseEntity.ok(ngReq);
    }
    
	// 마일리지 교환에서 사용하려고 만듦
	@ResponseBody
	@GetMapping("/paidnobel/get")
	public ResponseEntity<List<NovelItemDto>> searchPaidNovelByKeyword(@RequestParam String keyword) {
		List<NovelItemDto> novels = novelService.getPaidNovelByKeyword(keyword);
		
		return ResponseEntity.ok(novels);
	}
}
