package com.itwill.lightbooks.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Genre;
import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.NovelCreateDto;
import com.itwill.lightbooks.repository.genre.GenreRepository;
import com.itwill.lightbooks.repository.novel.NGenreRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NovelService {
	
	private final NovelRepository novelRepo;
	private final GenreRepository genreRepo;
	private final NGenreRepository ngenreRepo;
	
	@Transactional
	public Novel create(NovelCreateDto dto) {
		
		// 소설 저장
		Novel novel = novelRepo.save(dto.toEntity());
		log.info("저장되는 소설 = {}", novel);
		
		// 장르 정보 가져오기
		List<Genre> genres = genreRepo.findAllById(dto.getNovelGenreIds());
		log.info("총 장르 genres = {}",genres);
		
		// 소설 장르 저장
		List<NGenre> novelGenres = genres.stream()
				.map(genre -> NGenre.builder()
						.novel(novel)
						.genre(genre)
						.isMain(dto.getMainGenreId())
						.build())
				.collect(Collectors.toList());
		
		ngenreRepo.saveAll(novelGenres);
		log.info("해당 소설 장르 = {}", novelGenres);
		
		
		return novel;
	}

}
