package com.itwill.lightbooks.service;

import org.springframework.stereotype.Service;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.EpisodeCreateDto;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class EpisodeService {
	
	private final EpisodeRepository episodeRepo;
	private final NovelRepository novelRepo;

	@Transactional
	public Episode createEpisode(Long noverId, EpisodeCreateDto dto) {
        Novel novel = novelRepo.findById(dto.getNovelId())
               .orElseThrow();

       Episode episode = dto.toEntity(novel);

       return episodeRepo.save(episode);
   }
	
}
