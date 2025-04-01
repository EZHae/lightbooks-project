package com.itwill.lightbooks.repository.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
	List<ChatMessage> findByChatRoomIdOrderBySendTimeAsc(Long chatRoomId);
	
    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage cm SET cm.isRead = 1 WHERE cm.chatRoomId = :chatRoomId AND cm.isRead = 0")
    int updateIsReadByChatRoomId(Long chatRoomId);
}