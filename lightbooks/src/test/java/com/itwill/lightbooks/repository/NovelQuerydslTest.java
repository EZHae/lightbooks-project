package com.itwill.lightbooks.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

	@Test
	@Transactional
	public void testSearchById() {
		Novel entity = novelRepo.searchByIdWithGenre(1);
			
		System.out.println(entity.getTitle());
		assertNotNull(entity.getNovelGenre());
		
		
		entity.getNovelGenre().forEach(novelGenre -> {
	        System.out.println("NovelGenre ID: " + novelGenre.getId());
	        System.out.println("Genre: " + novelGenre.getGenre().getName());
	    });
		
	}
}
