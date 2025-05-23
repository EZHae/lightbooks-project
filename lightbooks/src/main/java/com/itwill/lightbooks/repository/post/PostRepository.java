package com.itwill.lightbooks.repository.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itwill.lightbooks.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
	
    @Query("SELECT p FROM Post p WHERE p.highlight = 1 ORDER BY p.modifiedTime ASC")
    List<Post> searchOnlyHighlight();

}
