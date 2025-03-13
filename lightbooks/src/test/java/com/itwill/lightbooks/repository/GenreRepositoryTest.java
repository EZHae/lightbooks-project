package com.itwill.lightbooks.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.itwill.lightbooks.domain.Genre;
import com.itwill.lightbooks.repository.genre.GenreRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class GenreRepositoryTest {
	
	@Autowired
	private GenreRepository genreRepo;
	@Test
	public void testFindByAll() {
		List<Genre> genre = genreRepo.findAll();
		log.info("genre={}", genre);
	}
	
}
