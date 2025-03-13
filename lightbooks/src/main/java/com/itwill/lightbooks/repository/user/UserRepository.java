package com.itwill.lightbooks.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.User;

public interface UserRepository extends JpaRepository<User, Long>, UserQuerydsl {

	Optional<User> findByLoginId(String loginId);
}