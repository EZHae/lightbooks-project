package com.itwill.lightbooks.repository.novel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.NovelGradeRequest;

public interface NovelGradeRequestRepository extends JpaRepository<NovelGradeRequest, Long>, NovelGradeQuerydsl {
	
	Page<NovelGradeRequest> findByUserIdAndNovelIdAndType(Long userId, Long novelId, int type, Sort sort);
	
}
