package com.itwill.lightbooks.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.repository.novel.NGenreRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class NGenreRepositoryTest {
	
	@Autowired
	private NGenreRepository ngenreRepo;
	
//	@Test
//	@Transactional
	public void testFindAll() {
		List<NGenre> savedGenres = ngenreRepo.findAll();
		log.info("저장된 소설 장르 리스트 = {}", savedGenres);
		
	}

}
