package com.itwill.lightbooks.repository.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	int countByUserIdAndIsRead(Long userId, int isRead);
	
	List<Notification> findByUserIdAndIsReadOrderByCreatedTimeDesc(Long userId, int isRead);
	
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = 1 WHERE n.userId = :userId AND n.isRead = 0")
    void updateNotificationsAllReadByUserId(Long userId);
    
    List<Notification> findByUserIdOrderByCreatedTimeDesc(Long userId);
    
    @Transactional
    void deleteByUserId(Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = 1 WHERE n.id = :id")
    void updateNotificationReadById(Long id);
}
