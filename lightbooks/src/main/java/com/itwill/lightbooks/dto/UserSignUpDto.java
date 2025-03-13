package com.itwill.lightbooks.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.itwill.lightbooks.domain.User;

import lombok.Data;

@Data
public class UserSignUpDto {

	private String loginId;
    private String password;
    private String nickname;
    private String username;
    private Integer gender;
    private Integer age;
    private String phonenumber;
    private String email;
    private String imgSrc;
    
    public User toEntity(PasswordEncoder passwordEncoder) {
    	return User.builder()
    			.loginId(loginId)
    			.password(passwordEncoder.encode(password))
    			.nickname(nickname)
    			.username(username)
    			.gender(gender)
    			.age(age)
    			.phonenumber(phonenumber)
    			.email(email)
    			.imgSrc(imgSrc)
    			.build();
    }
}
