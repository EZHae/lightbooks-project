package com.itwill.lightbooks.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Bookmark;
import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.EpisodeBuyDto;
import com.itwill.lightbooks.dto.LikedNovelBookmarkDto;
import com.itwill.lightbooks.dto.PurchasedNovelBookmarkDto;
import com.itwill.lightbooks.dto.RecentlyWatchedEpisodeDto;
import com.itwill.lightbooks.repository.bookmark.BookmarkRepository;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookmarkService {

	private final BookmarkRepository bookmarkRepo;
	private final UserRepository userRepo;
	private final NovelRepository novelRepo;
	private final EpisodeRepository epiRepo;
	private final EpisodeService epiService;
	
	
	// 유료 회차 구매 여부 확인
    public boolean isPurchasedByUser(Long userId, Long novelId, Long episodeId) {
        return bookmarkRepo.existsByUserIdAndNovelIdAndEpisodeIdAndType(userId, novelId, episodeId, 2); // type=2: 구매 작품
    }
    
    // 회차를 구매했을 때 북마크 저장 from EpisodeBuyDto
    public Bookmark saveBookmarkFromEpisodeBuyDto(EpisodeBuyDto dto) {
    	Bookmark bookmark = Bookmark.builder()
    			.user(userRepo.findById(dto.getUserId()).orElseThrow())
    			.novel(novelRepo.findById(dto.getNovelId()).orElseThrow())
    			.episode(epiRepo.findById(dto.getEpisodeId()).orElseThrow())
    			.type(2).build();
    	
    	Bookmark savedBookmark = bookmarkRepo.save(bookmark);
    	
    	return savedBookmark;
    }
    
    // 추가) 최근 본 회차 가져오기(공지 제외)
    public Page<RecentlyWatchedEpisodeDto> getRecentlyWatchedEpisodes(Long userId, int pageNo, Sort sort) {
        PageRequest pageRequest = PageRequest.of(pageNo, 10, sort);
        Page<com.itwill.lightbooks.domain.Bookmark> page = bookmarkRepo.findRecentlyWatchedEpisodesByUserIdExcludeNotice(userId, pageRequest);

        return page.map(bookmark -> {
            Episode episode = epiRepo.findById(bookmark.getEpisode().getId()).orElseThrow();
            Novel novel = novelRepo.findById(episode.getNovel().getId()).orElseThrow();
            RecentlyWatchedEpisodeDto dto = new RecentlyWatchedEpisodeDto();
            dto.setEpisodeId(episode.getId());
            dto.setEpisodeNum(episode.getEpisodeNum());
            dto.setEpisodeTitle(episode.getTitle());
            dto.setAccessTime(bookmark.getAccessTime());
            dto.setNovelTitle(novel.getTitle());
            dto.setNovelWriter(novel.getWriter());
            dto.setNovelGenres(novel.getNovelGenre() == null || novel.getNovelGenre().isEmpty()
            	    ? Collections.emptyList()
            	    : novel.getNovelGenre().stream()
            	          .map(genre -> genre.getGenre().getName())
            	          .collect(Collectors.toList()));
            dto.setCoverSrc(novel.getCoverSrc());
            return dto;
        });
    }
    
    
    // 추가) 사용자id와 회차id으로 북마크 검색
    public Bookmark findBookmarkByUserIdAndEpisodeId(Long userId, Long episodeId) {
        return bookmarkRepo.findByUserIdAndEpisodeId(userId, episodeId);
    }
    
    // 추가) 회차 상세보기 access_time(로드 시간) 업데이트
    @Transactional
    public void updateAccessTime(Long userId, Long episodeId) {
        Bookmark bookmark = bookmarkRepo.findByUserIdAndEpisodeId(userId, episodeId);

        if (bookmark != null) { //기존에 북마크 레코드가 존재하는 경우
            bookmark.updateAccessTime(LocalDateTime.now());
            bookmarkRepo.save(bookmark);
        } else { //기존 북마크 레코드가 존재하지 않는 경우
            Episode episode = epiRepo.findById(episodeId).orElseThrow();
            User user = userRepo.findById(userId).orElseThrow();
            if (episode != null && user != null) {
                Bookmark newBookmark = Bookmark.builder()
                        .user(user)
                        .episode(episode)
                        .type(1) // 최근 본 회차
                        .accessTime(LocalDateTime.now())
                        .build();
                bookmarkRepo.save(newBookmark);
            }
        }
    }
    
    // 추가) 좋아요 누른 소설 목록 조회
    @Transactional(readOnly = true)
    public Page<LikedNovelBookmarkDto> getLikedNovels(Long userId, int pageNo, Sort sort) {
        PageRequest pageRequest = PageRequest.of(pageNo, 10, sort);
        Page<Bookmark> page = bookmarkRepo.findLikedNovelsByUserIdOrderByLikeCountDesc(userId, pageRequest);

        return page.map(bookmark -> {
            Novel novel = novelRepo.findById(bookmark.getNovel().getId()).orElseThrow();
            LikedNovelBookmarkDto dto = new LikedNovelBookmarkDto();
            dto.setNovelId(novel.getId());
            dto.setNovelTitle(novel.getTitle());
            dto.setCoverSrc(novel.getCoverSrc());
            dto.setLikeCount(novel.getLikeCount()); // Novel 엔티티에서 좋아요 수 가져오기
            dto.setNovelwriter(novel.getWriter());
            dto.setNovelGenres(novel.getNovelGenre() == null || novel.getNovelGenre().isEmpty()
            	    ? Collections.emptyList()
            	    : novel.getNovelGenre().stream()
            	          .map(genre -> genre.getGenre().getName())
            	          .collect(Collectors.toList()));
            dto.setTotalViews(epiService.getTotalViewsByNovelId(novel.getId()).longValue());
            return dto;
        });
    }
    
    // 추가) 구매한 소설 목록 조회
    public Page<PurchasedNovelBookmarkDto> getPurchasedNovels(Long userId, int pageNo, Sort sort) {
        PageRequest pageRequest = PageRequest.of(pageNo, 10, sort);
        Page<Bookmark> page = bookmarkRepo.findPurchasedNovelsByUserIdOrderByCreatedTimeDesc(userId, pageRequest);

        return page.map(bookmark -> {
            Novel novel = novelRepo.findById(bookmark.getNovel().getId()).orElseThrow();
            PurchasedNovelBookmarkDto dto = new PurchasedNovelBookmarkDto();
            dto.setNovelId(novel.getId());
            dto.setNovelTitle(novel.getTitle());
            dto.setCoverSrc(novel.getCoverSrc());
            dto.setNovelWriter(novel.getWriter());
            dto.setNovelGenres(novel.getNovelGenre() == null || novel.getNovelGenre().isEmpty()
            	    ? Collections.emptyList()
            	    : novel.getNovelGenre().stream()
            	          .map(genre -> genre.getGenre().getName())
            	          .collect(Collectors.toList()));
            dto.setTotalViews(epiService.getTotalViewsByNovelId(novel.getId()).longValue());
            return dto;
        });
    }
    
    
    
    
    
    
    
    // ========================= 좋아요!! ============================
    /**
     * 
     * @param userId 사용자 아이디
     * @param novelId 유저 아이디
     * @return 좋아요 상태 (true: 좋아요 추가, false: 좋아요 취소)
     */
    // 좋아요 버튼 선택 및 해제 서비스
 	@Transactional
 	public boolean toggleLike(Long userId, Long novelId) {
 		
 		// 유저와 소설 엔터티 조회
 		User user = userRepo.findById(userId).orElseThrow();
		Novel novel = novelRepo.findById(novelId).orElseThrow();
 		
 		// 좋아요 여부 확인
 		boolean isLiked = bookmarkRepo.existsByUserIdAndNovelIdAndType(userId, novelId, 0);
 		
 		if(isLiked) {
 			// 좋아요 취소
 			bookmarkRepo.deleteByUserIdAndNovelIdAndType(userId, novelId, 0);
 			novelRepo.decreaseLikeCount(novelId); //좋아요 개수 감소
 			return false; // 좋아요 취소
 		} else { // 좋아요 추가
 			Bookmark bookmark = Bookmark.builder()
 					.user(user)
 					.novel(novel)
 					.type(0)
 					.accessTime(LocalDateTime.now())
 					.createdTime(LocalDateTime.now())
 					.build();
 			bookmarkRepo.save(bookmark);
 			novelRepo.increaseLikeCount(novelId);
 			return true;
 		}
 	}
 	
 	/**
 	 * 
 	 * @param novelId 소설 ID
 	 * @return 해당 소설의 좋아요 개수 조회
 	 */
 	@Transactional(readOnly = true)
 	@Cacheable(value = "novelLike", key = "#novelId")
 	public int getLikeCount(Long novelId) {
 		return bookmarkRepo.countByNovelIdAndType(novelId, 0);
 	}
 	
	// 로그인한 유저가 해당 소설에 좋아요를 했는가?
	@Transactional(readOnly = true)
	public boolean isLiked(Long userId, Long novelId) {
//		User user = userRepo.findById(userId).orElseThrow();
//		Novel novel = novelRepo.findById(novelId).orElseThrow();
		return bookmarkRepo.existsByUserIdAndNovelIdAndType(userId, novelId, 0);
	}
}
