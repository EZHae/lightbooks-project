package com.itwill.lightbooks.web;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.EpisodeListDto;
import com.itwill.lightbooks.dto.NovelCreateDto;
import com.itwill.lightbooks.dto.NovelListItemDto;
import com.itwill.lightbooks.dto.NovelResponseDto;
import com.itwill.lightbooks.dto.NovelSearchDto;
import com.itwill.lightbooks.dto.NovelUpdateDto;
import com.itwill.lightbooks.service.EpisodeService;
import com.itwill.lightbooks.service.NovelService;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/novel")
@Slf4j
@RequiredArgsConstructor
public class NovelController {
	
	private final NovelService novelService;
	private final EpisodeService episodeService; //추가
	
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
            @RequestParam(name = "page", defaultValue = "0") int pageNo) {
       log.info("소설 상세정보 페이지: {}",id);
       
       Novel novel = novelService.searchById(id);
       log.info("nove id = {}",novel);
       
       model.addAttribute("novel",novel);
       
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
        model.addAttribute("sort", sortStr.toString());         // 현재 정렬 방식 (문자열)
        model.addAttribute("firstEpisodeId", firstEpisodeId); // 첫 번째 에피소드의 ID
        model.addAttribute("novelId", id);
       
       return "novel/details";
    }

    
    // 내 작품 페이지
    @GetMapping("/my-works")
    public String myWorks(@RequestParam(name = "id") Long id, Model model) {
    	log.info("myWorks()");
    	log.info("현재 접속 아이디: {}", id);
    	
    	List<NovelResponseDto> novels = novelService.getNovelByUserId(id);
    	log.info("novels : {}", novels);
    	
    	model.addAttribute("novels", novels);
    	
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
    

    
 
}
