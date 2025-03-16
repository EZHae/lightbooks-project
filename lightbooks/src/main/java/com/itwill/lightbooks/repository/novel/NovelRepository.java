package com.itwill.lightbooks.repository.novel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Novel;

public interface NovelRepository extends JpaRepository<Novel, Long>, NovelQuerydsl {
	
	List<Novel> findByUserId(Long userId);

	   // JPQL
    // 제목 또는 내용에 포함된 문자열 대소문자 구분없이 검색
    // findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String t, String c, Pageable p)
//    @Query("select n from Novel n "
//            + "where upper(n.title) like upper('%' || :keyword || '%') "
//            + "or upper(n.writer) like upper('%' || :keyword || '%') ")
//    Page<Novel> findByTitleOrWriter(String keyword, Pageable pageable);
}
