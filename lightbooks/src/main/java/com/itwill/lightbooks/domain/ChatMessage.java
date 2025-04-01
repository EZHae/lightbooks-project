package com.itwill.lightbooks.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "CHAT_MESSAGE")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 메시지 ID
    
    @Column(name = "sender_id")
    private Long senderId;  // 메시지를 보낸 사람
    
    @Column(name = "sender_login_id")
    private String senderLoginId;
    
    @Column(name = "receiver_id")
    private Long receiverId;  // 메시지를 받는 사람
    
    @Column(name = "receiver_login_id")
    private String receiverLoginId;
    
    @Column(name = "chat_room_id")
    private Long chatRoomId;  // 해당 메시지가 속한 채팅방

    private String content;  // 메시지 내용

    @Column(name = "send_time", nullable = false, updatable = false, insertable = false)
    private LocalDateTime sendTime;  // 메시지 전송 시간
    
    @Column(name = "is_read", nullable = false, insertable = false)
    private int isRead;
}
