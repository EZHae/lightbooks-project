package com.itwill.lightbooks.repository.novel;

import com.itwill.lightbooks.domain.Novel;

public interface NovelQuerydsl {
	
	Novel searchById(Integer id);
	
	Novel searchByIdWithGenre(Integer id);
}
