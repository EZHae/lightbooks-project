package com.itwill.lightbooks.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.EpisodeCreateDto;
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
	@Transactional(readOnly = true)
    public Long findNextEpisodeId(Long novelId, Integer currentEpisodeNum) {
        return episodeRepo.findFirstByNovelIdAndEpisodeNumGreaterThanOrderByEpisodeNumAsc(novelId, currentEpisodeNum)
                .map(Episode::getId)
                .orElse(null);
    }
	
	//이전 회차 번호 찾기
	@Transactional(readOnly = true)
    public Long findPreviousEpisodeId(Long novelId, Integer currentEpisodeNum) {
		return episodeRepo.findFirstByNovelIdAndEpisodeNumLessThanOrderByEpisodeNumDesc(novelId, currentEpisodeNum)
                .map(Episode::getId)
                .orElse(null);
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
	
}
