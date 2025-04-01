package com.itwill.lightbooks.repository.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itwill.lightbooks.domain.ChatRoom;
import com.itwill.lightbooks.dto.ChatRoomWithIsReadDto;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	
	@Query("""
		    SELECT new com.itwill.lightbooks.dto.ChatRoomWithIsReadDto(
		        cr.id,
		        cr.userId,
		        cr.userLoginId,
		        COALESCE(MAX(cm.sendTime)),
		        COUNT(CASE WHEN cm.isRead = 0 THEN 1 ELSE NULL END)
		    )
		    FROM ChatRoom cr
		    LEFT JOIN ChatMessage cm ON cr.id = cm.chatRoomId
		    GROUP BY cr.id, cr.userId, cr.userLoginId
		    ORDER BY 
		        MAX(CASE WHEN cm.isRead = 0 THEN 1 ELSE 0 END) DESC,
		        MAX(cm.sendTime) DESC
		""")
		List<ChatRoomWithIsReadDto> findAllSortedByLatestMessage();
}