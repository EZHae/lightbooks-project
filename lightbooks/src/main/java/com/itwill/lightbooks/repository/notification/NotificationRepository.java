package com.itwill.lightbooks.repository.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	int countByUserIdAndIsRead(Long userId, int isRead);
	
	List<Notification> findByUserIdAndIsRead(Long userId, int isRead);
}
