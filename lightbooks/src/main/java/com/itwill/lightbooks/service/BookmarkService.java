package com.itwill.lightbooks.service;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Bookmark;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.EpisodeBuyDto;
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
