package com.itwill.lightbooks.service;

import org.springframework.stereotype.Service;

import com.itwill.lightbooks.repository.bookmark.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookmarkService {

	private final BookmarkRepository bookmarkRepo;
	
	// 유료 회차 구매 여부 확인
    public boolean isPurchasedByUser(Long userId, Long novelId, Long episodeId) {
        return bookmarkRepo.existsByUserIdAndNovelIdAndEpisodeIdAndDiv(userId, novelId, episodeId, 2); // div=2: 구매 작품
    }
}
