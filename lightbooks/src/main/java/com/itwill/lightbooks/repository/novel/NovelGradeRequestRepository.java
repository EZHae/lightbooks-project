package com.itwill.lightbooks.repository.novel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.NovelGradeRequest;

public interface NovelGradeRequestRepository extends JpaRepository<NovelGradeRequest, Long>, NovelGradeQuerydsl {

	List<NovelGradeRequest> findByUserId(Long userId);

	Optional<NovelGradeRequest> findByUserIdAndNovelId(Long currentUserId, Long novelId);
}
