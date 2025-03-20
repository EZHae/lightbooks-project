package com.itwill.lightbooks.repository.novel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itwill.lightbooks.domain.NovelGradeRequest;
import com.itwill.lightbooks.dto.NovelSearchGradeDto;

public interface NovelGradeQuerydsl {

	Page<NovelGradeRequest> searchByKeyword(NovelSearchGradeDto dto,Pageable pageable);

}
