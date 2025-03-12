package com.itwill.lightbooks.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.NGenre;
import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.repository.novel.NovelRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class NovelRepositoryTest {
	
	@Autowired
	private NovelRepository novelRepo;

//	@Test
//	@Transactional
	public void testFindAll() {
		List<Novel> list = novelRepo.findAll();
		list.forEach(System.out::println);
	}
	
}
