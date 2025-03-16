package com.itwill.lightbooks.repository.user;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.itwill.lightbooks.domain.QUser;
import com.itwill.lightbooks.domain.User;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserQuerydslImpl extends QuerydslRepositorySupport implements UserQuerydsl {

    public UserQuerydslImpl() {
		// 상위 클래스 QuerydslRepositorySuppor는 기본 생성자를 갖고 있지 않기 때문에
		// 하위 클래스의 생성자에서는 아규먼틀르 갖는 생성자를 명시적으로 호출해야함.
        super(User.class);
    }

    @Override
    public User searchById(Long id) {
        log.info("searchById(id={})", id);
        QUser user = QUser.user;
        JPQLQuery<User> query = from(user)
            .where(user.id.eq(id));
        
        User entity = query.fetchOne();

        return entity;
    }

}