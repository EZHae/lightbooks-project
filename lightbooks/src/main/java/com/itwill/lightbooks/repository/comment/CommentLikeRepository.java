package com.itwill.lightbooks.repository.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.lightbooks.domain.Comment;
import com.itwill.lightbooks.domain.CommentLike;
import com.itwill.lightbooks.domain.User;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>{

	boolean existsByUserIdAndCommentId(Long userId, Long commentId);

	CommentLike findByUserAndComment(User user, Comment comment);

	int countByCommentId(Long commentId);

	@Query("SELECT cl.comment.id FROM CommentLike cl WHERE cl.user.id = :userId")
	List<Long> findLikedCommentIdsByUser(@Param("userId") Long userId);

}
