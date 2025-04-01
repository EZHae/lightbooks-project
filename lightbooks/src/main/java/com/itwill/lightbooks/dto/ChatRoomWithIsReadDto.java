package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomWithIsReadDto {

    private Long id;  // 채팅방 ID
    private Long userId; // 개설자 아이디
    private String userLoginId; // 개설자 로그인 아이디
    private LocalDateTime sendTime;  // 메시지 전송 시간
    private Long isReadCount;
}
