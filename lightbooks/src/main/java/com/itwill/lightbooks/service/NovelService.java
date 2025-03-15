package com.itwill.lightbooks.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Genre;
import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.NovelCreateDto;
import com.itwill.lightbooks.dto.NovelResponseDto;
import com.itwill.lightbooks.repository.genre.GenreRepository;
import com.itwill.lightbooks.repository.novel.NGenreRepository;
import com.itwill.lightbooks.repository.novel.NovelRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NovelService {
	
	private final NovelRepository novelRepo;
	private final GenreRepository genreRepo;
	private final NGenreRepository ngenreRepo;
	
	
	public List<NovelResponseDto> getNovelByUserId(Long userId) {
		List<Novel> novels = novelRepo.searchByUserIdWithGenre(userId);
		
		log.info("해당 유저 소설 : {}", novels);
		
		return novels.stream().map(novel -> new NovelResponseDto(
				novel.getId(),
				novel.getTitle(),
				novel.getIntro(),
				novel.getWriter(),
				novel.getCoverSrc(),
				novel.getLikeCount(),
				novel.getState(),
				novel.getNovelGenre()
				.stream().map(novelGenre -> novelGenre.getGenre().getName())
				.collect(Collectors.toList())
				))
				.collect(Collectors.toList());
	}
	
	
	public Novel searchById(Long id) {
		log.info("searchId()");
		Novel novel = novelRepo.findById(id).orElseThrow();
		
		return novel;
	}
	
	public List<Novel> searchAll() {
		log.info("searchAll()");
		List<Novel> list = novelRepo.findAll();
		
		log.info("search list = {}", list);
		
		return list;
	}
	
	@Transactional
	public Novel create(NovelCreateDto dto) {
		
		// 소설 저장
		Novel novel = novelRepo.save(dto.toEntity());
		log.info("저장되는 소설 = {}", novel);
		
		log.info("받은 장르 ID 목록 = {}", dto.getGenre()); // 입력 확인
		// 장르 정보 가져오기
		List<Genre> selectedGenres = genreRepo.findAllById(dto.getGenre());
		log.info("총 장르 genres = {}",selectedGenres);
		
		// 소설 장르 저장
		List<NGenre> novelGenres = selectedGenres.stream()
				.map(genre -> NGenre.builder()
						.novel(novel)
						.genre(genre)
						.build())
				.collect(Collectors.toList());
		
		ngenreRepo.saveAll(novelGenres);
		log.info("해당 소설 장르 = {}", novelGenres);
		
		return novel;
	}

}
