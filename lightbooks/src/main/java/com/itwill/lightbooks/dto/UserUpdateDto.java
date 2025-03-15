package com.itwill.lightbooks.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.itwill.lightbooks.domain.User;

import lombok.Data;

@Data
public class UserUpdateDto {

    private String password;
    private String nickname;
    private String phonenumber;
    private String email;
    private String imgSrc;
    
    public User toEntity(PasswordEncoder passwordEncoder) {
    	return User.builder()
    			.password(passwordEncoder.encode(password))
    			.nickname(nickname)
    			.phonenumber(phonenumber)
    			.email(email)
    			.imgSrc(imgSrc)
    			.build();
    }
}
