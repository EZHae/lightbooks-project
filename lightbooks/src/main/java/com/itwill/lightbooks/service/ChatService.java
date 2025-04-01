package com.itwill.lightbooks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.itwill.lightbooks.domain.ChatMessage;
import com.itwill.lightbooks.domain.ChatRoom;
import com.itwill.lightbooks.dto.ChatRoomWithIsReadDto;
import com.itwill.lightbooks.repository.chat.ChatMessageRepository;
import com.itwill.lightbooks.repository.chat.ChatRoomRepository;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    
    // 메시지 저장 메서드
    public void saveMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }
    
    // 채팅방 리스트 불러오기
    public List<ChatRoomWithIsReadDto> readChatRoomList() {
    	List<ChatRoomWithIsReadDto> chatRoomList = chatRoomRepository.findAllSortedByLatestMessage();
    	
    	return chatRoomList;
    }
    
    // 채팅메시지 불러오기
    public List<ChatMessage> readChatMessageByChatRoomId(Long chatRoomId) {
    	List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomIdOrderBySendTimeAsc(chatRoomId);
    	chatMessageRepository.updateIsReadByChatRoomId(chatRoomId);    	
    	return chatMessages;
    }
    
    // 채팅방 있는지 검사
    public Boolean checkChatRoomById(Long id) {
    	Optional<ChatRoom> chatRoom = chatRoomRepository.findById(id);
        return chatRoom.isPresent(); // 값이 있으면 true, 없으면 false 반환

    }
    
    // 채팅방 생성
    public void createChatRoom(Long userId, String loginId) {
    	ChatRoom chatroom = ChatRoom.builder().id(userId).userId(userId).userLoginId(loginId).build();
    	chatRoomRepository.save(chatroom);
    }
}
