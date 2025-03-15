package com.itwill.lightbooks.dto;

import lombok.Data;

@Data
public class UserUpdatePasswordDto {
	
	private Long id;
	private String oldPassword;
	private String newPassword;
	
}
