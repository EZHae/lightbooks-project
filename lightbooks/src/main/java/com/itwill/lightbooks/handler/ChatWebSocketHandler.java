package com.itwill.lightbooks.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.itwill.lightbooks.domain.ChatMessage;
import com.itwill.lightbooks.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler {

    private final ChatService chatService;

    @MessageMapping("/sendMessage") // "/app/sendMessage"로 오는 메시지를 처리
    @SendTo("/topic/messages") // "/topic/messages"로 메시지를 전송
    public ChatMessage sendMessage(Message<ChatMessage> message) {
    	log.info("ChatWebSocketHandler::sendMessage");
    	log.info("message=", message);
        ChatMessage chatMessage = message.getPayload();
        chatService.saveMessage(chatMessage); // DB에 메시지 저장
        return chatMessage; // 채팅 메시지 전송
    }
    
    @MessageMapping("/newChatRoom")
    @SendTo("/topic/admin")
    public String notifyNewChatRoom(Message<String> message) {
        log.info("ChatWebSocketHandler::notifyNewChatRoom");
        return message.getPayload();  // 어드민에게 메시지 전송
    }
}
