package com.itwill.lightbooks.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Comment;
import com.itwill.lightbooks.domain.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>{

	boolean existsByUserIdAndCommentId(Long userId, Long commentId);

	CommentLike findByUserIdAndCommentId(Long commentId, Long userId);

	

}
