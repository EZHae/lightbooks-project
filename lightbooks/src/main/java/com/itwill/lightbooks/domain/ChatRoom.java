package com.itwill.lightbooks.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "CHAT_ROOM")
public class ChatRoom extends BaseTimeEntity {

    @Id
    private Long id;  // 채팅방 ID
    
    @Column(name = "user_id")
    private Long userId; // 개설자 아이디
    
    @Column(name = "user_login_id")
    private String userLoginId; // 개설자 로그인 아이디
}
