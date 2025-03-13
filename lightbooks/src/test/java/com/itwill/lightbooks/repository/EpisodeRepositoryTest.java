package com.itwill.lightbooks.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.itwill.lightbooks.domain.Episode;
import com.itwill.lightbooks.repository.episode.EpisodeRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class EpisodeRepositoryTest {
	
	@Autowired
	private EpisodeRepository epiRepo;
	
//	@Test
	@Transactional
	void testFindAll() {
		
		List<Episode> list = epiRepo.findAll();
		list.forEach(System.out::println);
		
		assertThat(list.size()).isEqualTo(5);
	}
	
//	@Test
	void testFindById() {
		
		Optional<Episode> list = epiRepo.findById(1L);
		System.out.println(list);
	}

}
