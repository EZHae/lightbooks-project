package com.itwill.lightbooks.repository.novel;

import java.util.List;

import com.itwill.lightbooks.domain.Novel;

public interface NovelQuerydsl {
	
	Novel searchById(Long id);
	
	Novel searchByIdWithGenre(Long id);

	List<Novel> searchByUserIdWithGenre(Long userId);
	
	
}
