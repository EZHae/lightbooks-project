package com.itwill.lightbooks.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.dto.UserSignUpDto;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String loginId = username;
		log.info("loginId={}", loginId);
		
		Optional<User> user = userRepo.findByLoginId(loginId);
		log.info("{}", user);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new UsernameNotFoundException(loginId + "과 일치하는 사용자 없음");
		}
	}
	
	@Transactional
	public User create(UserSignUpDto dto) {
		log.info("dto={}", dto);
		
		User savedUser = userRepo.save(dto.toEntity(passwordEncoder));
		
		return savedUser;
	}
}
