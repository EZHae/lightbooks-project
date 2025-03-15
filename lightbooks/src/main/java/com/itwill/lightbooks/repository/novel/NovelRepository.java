package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Novel;

public interface NovelRepository extends JpaRepository<Novel, Long>, NovelQuerydsl {
	
	List<Novel> findByUserId(Long userId);
	
}
