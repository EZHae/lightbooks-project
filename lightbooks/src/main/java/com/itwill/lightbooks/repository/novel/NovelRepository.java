package com.itwill.lightbooks.repository.novel;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Novel;

public interface NovelRepository extends JpaRepository<Novel, Integer>, NovelQuerydsl {
	
}
