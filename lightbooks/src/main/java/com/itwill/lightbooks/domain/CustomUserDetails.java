package com.itwill.lightbooks.domain;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails extends User implements UserDetails{
	
	private Long id;
	
	public CustomUserDetails(User user) {
		super();
		this.id = user.getId();
	}
	public Long getId() {
		return id;
	}
}
