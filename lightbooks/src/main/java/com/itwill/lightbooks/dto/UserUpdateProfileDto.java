package com.itwill.lightbooks.dto;

import com.itwill.lightbooks.domain.User;

import lombok.Data;

@Data
public class UserUpdateProfileDto {

	private Long id;
	private String username;
    private String nickname;
    private String phonenumber;
    private String email;
    private String imgSrc;
    
    public User toEntity() {
    	return User.builder()
    			.id(id)
    			.username(username)
    			.nickname(nickname)
    			.phonenumber(phonenumber)
    			.email(email)
    			.imgSrc(imgSrc)
    			.build();
    }
}
