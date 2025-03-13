package com.itwill.lightbooks.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Novel;
import com.itwill.lightbooks.dto.NovelCreateDto;
import com.itwill.lightbooks.repository.novel.NovelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NovelService {
	
	private final NovelRepository novelRepo;
	
	@Transactional
	public Novel create(NovelCreateDto dto) {
		Novel entity = novelRepo.save(dto.toEntity());
		
		return entity;
	}

}
