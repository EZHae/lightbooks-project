package com.itwill.lightbooks.repository.user;

import com.itwill.lightbooks.domain.User;

public interface UserQuerydsl {

    User searchById(Long id);
}
