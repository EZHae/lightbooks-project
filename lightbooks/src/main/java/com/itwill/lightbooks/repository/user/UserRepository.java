package com.itwill.lightbooks.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.User;

public interface UserRepository extends JpaRepository<User, Long>, UserQuerydsl {

}