package com.itwill.lightbooks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.domain.UserWallet;
import com.itwill.lightbooks.dto.UserSignUpDto;
import com.itwill.lightbooks.dto.UserUpdatePasswordDto;
import com.itwill.lightbooks.dto.UserUpdateProfileDto;
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
	
	public List<User> read() {
		List<User> users = userRepo.findAll();
		return users;
	}
	
	public User searchById(Long id) {
		
		Optional<User> user = userRepo.findById(id);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public User searchByLoginId(String loginId) {
		
		Optional<User> user = userRepo.findByLoginId(loginId);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public User searchByNickname(String nickname) {
		
		Optional<User> user = userRepo.findByNickname(nickname);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public User searchByPhonenumber(String phonenumber) {
		
		Optional<User> user = userRepo.findByPhonenumber(phonenumber);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public User searchByEmail(String email) {
		
		Optional<User> user = userRepo.findByEmail(email);
		if (user.isEmpty()) {
			return null;
		} else {
			return user.get();
		}
	}
	
	public UserWallet SearchUserWalletByUserId(Long id) {
		
		User user = userRepo.findById(id).orElseThrow();
		UserWallet userWallet = user.getUserWallet();
		
		return userWallet;
	}
	
	@Transactional
	public User create(UserSignUpDto dto) {
		log.info("dto={}", dto);
		
		User savedUser = userRepo.save(dto.toEntity(passwordEncoder));
		
		return savedUser;
	}
	
	public void updateProfile(UserUpdateProfileDto dto) {
		User user = userRepo.findById(dto.getId()).orElseThrow();
		
		user.updateProfile(dto.getUsername(), dto.getNickname(), dto.getPhonenumber(), dto.getEmail());
		
		userRepo.save(user);
	}
	
	public Boolean checkPassword(UserUpdatePasswordDto dto) {
		User user = userRepo.findById(dto.getId()).orElseThrow();
		
		if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
			return true;
		} else {
			return false;
		}
	}
	
	public void updatePassword(UserUpdatePasswordDto dto) {
		User user = userRepo.findById(dto.getId()).orElseThrow();
		
		String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
		user.updatePassword(encodedNewPassword);
		
		userRepo.save(user);
	}
	
	public void deleteUser(Long id) {
		userRepo.deleteById(id);
	}
}
