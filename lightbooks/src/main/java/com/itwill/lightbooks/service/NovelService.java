package com.itwill.lightbooks.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Genre;
import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.NovelCreateDto;
import com.itwill.lightbooks.dto.NovelListItemDto;
import com.itwill.lightbooks.dto.NovelResponseDto;
import com.itwill.lightbooks.dto.NovelSearchDto;
import com.itwill.lightbooks.dto.NovelUpdateDto;
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
	
	// 작품 수정 페이지에 데이터를 가져옴
	public Novel searchByIdWithGenre(Long id) {
		Novel novel = novelRepo.searchByIdWithGenre(id);
		return novel;
	}
	
	// 내 작품 페이지에 유저 아이디로 데이터를 가져옴
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
				.collect(Collectors.toList()),
				novel.getRating() // 데시멀이라서 stream할 필요없음
				))
				.collect(Collectors.toList());
	}
	
	// 소설 하나만 가져오는 기능
	public Novel searchById(Long id) {
		log.info("searchId()");
		Novel novel = novelRepo.findById(id).orElseThrow();
		
		String genre = novel.getNovelGenre().isEmpty() ? "장르 없음" : novel.getNovelGenre().get(0).getGenre().getName();
		log.info("장르2 : {}", genre);
		return novel;
	}
	
	// 모든 소설 리스트
	public List<Novel> searchAll() {
		log.info("searchAll()");
		List<Novel> list = novelRepo.findAll();
		
		log.info("search list = {}", list);
		
		return list;
	}
	
	// 소설 생성
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
						.isMain(1)
						.build())
				.collect(Collectors.toList());
		
		ngenreRepo.saveAll(novelGenres);
		log.info("해당 소설 장르 = {}", novelGenres);
		
		return novel;
	}
	
	// 소설 삭제
	@Transactional
	public void deleteById(Long id) {
		novelRepo.deleteById(id);
	}

	public void updateNovel(NovelUpdateDto dto) {
		novelRepo.updateNovel(dto.getId(), dto.getTitle(), dto.getIntro(), dto.getCoverSrc(), dto.getAgeLimit(),
				dto.getState(), dto.getDays(), dto.getGenreId());
		
		log.info("genreID : {}", dto.getGenreId());
	}

	// 제목, 작성자로 소설 검색과 페이징
	public Page<NovelListItemDto> search(NovelSearchDto dto, Sort sort) {
		
		Pageable pageable = PageRequest.of(dto.getP(), 10, sort);
		Page<Novel> result = novelRepo.searchByKeyword(dto , pageable);
		
		return result.map(NovelListItemDto::fromEntity);
	}
	
	//추가
	// 소설 작성자 여부 확인 메서드
    public boolean isUserOwnerOfNovel(Long novelId, Long userId) {
        Novel novel = novelRepo.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 소설을 찾을 수 없습니다. ID: " + novelId));

        // Novel의 userId와 현재 사용자의 userId 비교
        return novel.getUserId().equals(userId);
    }

    public String getMainGenre(Novel novel) {
   	return novel.getNovelGenre()
    			.stream()
    			.filter(novelGenre -> novelGenre.getIsMain() == 1)
    			.map(novelGenre -> novelGenre.getGenre().getName())
    			.findFirst()
    			.orElse("장르없음");
    } 
    
}
