package com.itwill.lightbooks.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class UserQuerydslTest {

    @Autowired
    private UserRepository userRepo;

    @Test
    public void testSearchById() {
        User user = userRepo.searchById(1L);
        log.info("user(id=1): {}", user);
        log.info("userWallet={}", user.getUserWallet());
    }
}
