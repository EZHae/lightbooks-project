package com.itwill.lightbooks.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.repository.novel.NovelRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class NovelQuerydslTest {
	
	@Autowired
	private NovelRepository novelRepo;

//	@Test
//	@Transactional
	public void testSearchById() {
		Novel entity = novelRepo.searchByIdWithGenre(1L);
			
		assertNotNull(entity);
		
		log.info("총 소설 내용 : {}", entity);
		
		entity.getNovelGenre().forEach(novelGenre -> {
			System.out.println("novel : " + novelGenre);
	        System.out.println("NovelGenre ID: " + novelGenre.getId());
	    });
	}
	
//	@Test
	@Transactional
	public void testSearchByUserId() {
		List<Novel> entity = novelRepo.searchByUserIdWithGenre(6L);
			
		assertNotNull(entity);
		
		log.info("총 소설 내용 : {}", entity);
		
		entity.forEach(novel -> {
			System.out.println("novel : " + novel);
	        System.out.println("NovelGenre ID: " + novel.getId());
	        
	        novel.getNovelGenre().forEach(genre -> {
	        	System.out.println("genre : " + genre);
	        	System.out.println("genre name : " + genre.getGenre().getName());
	        });
	    });
		
	}
}
