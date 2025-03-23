package com.itwill.lightbooks.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.EpisodeCreateDto;
import com.itwill.lightbooks.dto.EpisodeListDto;
import com.itwill.lightbooks.dto.EpisodeUpdateDto;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class EpisodeService {
	
	private final EpisodeRepository episodeRepo;
	private final NovelRepository novelRepo;

	//에피소드 새글 작성
	@Transactional
	public Episode createEpisode(Long novelId, EpisodeCreateDto dto) {
		log.info("createEpisode(novelId={}, dto={})", novelId, dto);
		
        Novel novel = novelRepo.findById(novelId)
               .orElseThrow();

       Episode episode = dto.toEntity(novel);

       return episodeRepo.save(episode);
	}
	
	// 에피소드id로 에피소드 가져오기
	@Transactional(readOnly = true)
	public Episode getEpisodeById(Long episodeId) {
		log.info("getEpisodeById(episodeId={})", episodeId);
		
		return episodeRepo.findByIdWithNovel(episodeId)
				.orElseThrow(); 
	}
	
	//다음 회차 번호 찾기
//	@Transactional(readOnly = true)
//    public Long findNextEpisodeId(Long novelId, Integer currentEpisodeNum) {
//        return episodeRepo.findFirstByNovelIdAndEpisodeNumGreaterThanOrderByEpisodeNumAsc(novelId, currentEpisodeNum)
//                .map(Episode::getId)
//                .orElse(null);
//    }
//	
//	//이전 회차 번호 찾기
//	@Transactional(readOnly = true)
//    public Long findPreviousEpisodeId(Long novelId, Integer currentEpisodeNum) {
//		return episodeRepo.findFirstByNovelIdAndEpisodeNumLessThanOrderByEpisodeNumDesc(novelId, currentEpisodeNum)
//                .map(Episode::getId)
//                .orElse(null);
//    }
	
	//지우지마세요!
	//현재 회차 번호보다 작은 번호 중에서 공지가 아닌 가장 큰 회차번호 찾기(이전화, 다음화 버튼)
	public Long findPreviousEpisodeId(Long novelId, int currentEpisodeNum) {
        return episodeRepo.findPreviousEpisodeId(novelId, currentEpisodeNum);
    }

	//지우지마세요!
	//현재 회차 번호보다 큰 번호 중에서 공지가 아닌 가장 작은 회차번호 찾기(이전화, 다음화 버튼)
    public Long findNextEpisodeId(Long novelId, int currentEpisodeNum) {
        return episodeRepo.findNextEpisodeId(novelId, currentEpisodeNum);
    }
	
	//에피소드 마지막 회차번호 찾기
	@Transactional(readOnly = true)
	public Integer findMaxEpisodeNumByNovelId(Long novelId) {
    	return episodeRepo.findMaxEpisodeNumByNovelId(novelId);
    }
	
	//에피소드 수정
	@Transactional
    public Episode updateEpisode(EpisodeUpdateDto dto) {
        log.info("updateEpisode(dto={})", dto);

        Episode episode = episodeRepo.findById(dto.getId())
                .orElseThrow();

        if (!episode.getNovel().getId().equals(dto.getNovelId())) {
            throw new IllegalArgumentException("소설 정보가 일치하지 않습니다.");
        }
        
        episode.update(dto.getTitle(), dto.getContent());

        return episode;
    }
	
	//소설 상세보기에서 회차 리스트 
	@Transactional(readOnly = true)
    public Page<EpisodeListDto> getEpisodesByNovelAndCategory(Long novelId, Integer category, String sortOrder, Pageable pageable) {
        log.info("getEpisodesByNovelAndCategory(novelId={}, category={}, sortOrder={}, pageable={})",
                novelId, category, sortOrder, pageable);

        Novel novel = novelRepo.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid novel ID: " + novelId));

        Page<Episode> episodesPage;

        if (category == null) {
            // 카테고리가 지정되지 않은 경우: 공지 제외, episodeNum 정렬
            if ("episodeNum,desc".equals(sortOrder)) {
                episodesPage = episodeRepo.findByNovelAndCategoryNotOrderByEpisodeNumDesc(novel, 0, pageable);
            } else {
                episodesPage = episodeRepo.findByNovelAndCategoryNotOrderByEpisodeNumAsc(novel, 0, pageable);
            }
        } else if (category == 0) {
            // 카테고리가 공지인 경우: createdTime 정렬
            if ("createdTime,desc".equals(sortOrder)) {
                episodesPage = episodeRepo.findByNovelAndCategoryOrderByCreatedTimeDesc(novel, category, pageable);
            } else {
                //  "첫 화 부터" 버튼은 눌릴일이 없지만, 혹시 모르니..
                episodesPage = episodeRepo.findByNovelAndCategoryOrderByCreatedTimeAsc(novel, category, pageable); //메서드 추가해야함.
            }

        }
        else {
            // category가 공지가 아닌 경우 (일반 회차): episodeNum 정렬
            if ("episodeNum,desc".equals(sortOrder)) {
              episodesPage = episodeRepo.findByNovelAndCategoryOrderByEpisodeNumDesc(novel, category, pageable);
            } else {
              episodesPage = episodeRepo.findByNovelAndCategoryOrderByEpisodeNumAsc(novel, category, pageable);
            }
        }

        // Page<Episode> -> Page<EpisodeListDto> 변환
        return episodesPage.map(this::convertToDto);
    }
      
	// Episode 엔티티를 EpisodeListDto로 변환하는 메서드 (private)
    private EpisodeListDto convertToDto(Episode episode) {
        return new EpisodeListDto(
                episode.getId(),
                episode.getNovel().getId(),
                episode.getEpisodeNum(),
                episode.getTitle(),
                episode.getViews(),
                episode.getCategory(),
                episode.getCreatedTime()
        );
    }
    
	// 소설 상세페이지에서 첫화 보기
	public Optional<Episode> findFirstEpisodeByNovel(Long novelId) {
        Novel novel = novelRepo.findById(novelId).orElseThrow();
        return episodeRepo.findFirstByNovelOrderByEpisodeNumAsc(novel);
    }
	
	// 조회수 증가 메서드
    @Transactional
    public void increaseViewCount(Long episodeId) {
        log.info("increaseViewCount(episodeId={})", episodeId);
        Episode episode = episodeRepo.findById(episodeId).orElseThrow();
        episode.increaseViews();
        // episodeRepository.save(episode);  //필요 없음(JPA dirty checking)
    }
    
    //회차 삭제 메서드
    @Transactional
    public void deleteEpisode(Long episodeId) {
        log.info("deleteEpisode(episodeId={})", episodeId);

        // 삭제 전에 해당 ID의 에피소드가 존재하는지 확인 (선택 사항)
        if (!episodeRepo.existsById(episodeId)) {
            throw new NoSuchElementException("Episode not found with ID: " + episodeId);
        }

        episodeRepo.deleteById(episodeId);
    }
    
    //회차 create 입력시 회차번호가 이미 존재하는지
    public boolean doesEpisodeNumExist(Long novelId, int episodeNum) {
        return episodeRepo.existsByNovelIdAndEpisodeNum(novelId, episodeNum);
    }
    
//    //소유자 확인 메서드(필요시 사용하려고....)
//    public boolean isOwner(Long episodeId, Long userId) {
//        // 에피소드 조회
//        Episode episode = episodeRepo.findById(episodeId)
//                .orElseThrow(() -> new NoSuchElementException("해당 에피소드가 존재하지 않습니다. (ID: " + episodeId + ")"));
//
//        //소유자 확인 (Novel을 거쳐 User의 ID 비교)
//        if (episode.getNovel() == null || episode.getNovel().getUserId() == null) {
//            return false; // Novel 또는 User 정보가 없으면 false 반환
//        }
//        return episode.getNovel().getUserId().equals(userId);
//    }
	
    @Transactional(readOnly = true)
    public Long getEpisodeCountByNovelId(Long novelId) {
    	return episodeRepo.countNovelId(novelId);
    }
    
    //소설의 조회수(유/무료 회차 조회수만 sum)
    public Integer getTotalViewsByNovelId(Long novelId) {
        Integer totalViews = episodeRepo.sumViewsByNovelIdExcludingNotices(novelId);
        return totalViews != null ? totalViews : 0;
    }
}
