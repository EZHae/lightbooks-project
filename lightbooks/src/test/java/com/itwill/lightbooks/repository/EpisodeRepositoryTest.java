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
//	@Transactional
	public void testFindAll() {
		
		List<Episode> list = epiRepo.findAll();
		list.forEach(System.out::println);
		
		assertThat(list.size()).isEqualTo(5);
	}
	
//	@Test
	public void testFindById() {
		
		Optional<Episode> list = epiRepo.findById(1L);
		System.out.println(list);
	}
	
//	@Test
//	void testSave() {
//		Episode episode = Episode.builder().novel(null)
//	}
	
//	@Test
//	public void testUpdate() {
//		Episode episode = epiRepo.findById(5L).orElseThrow();
//		log.info("findById 결과 = {}", episode);
//		
//		episode.update(4, "4화", "4화 내용", 2);
//		log.info("update메서드 호출 = {}", episode);
//		
//		epiRepo.save(episode);
//		log.info("save메서드 호출 후 = {}", episode);
//		
//	}

}
