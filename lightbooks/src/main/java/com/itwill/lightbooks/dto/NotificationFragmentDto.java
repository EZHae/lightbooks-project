package com.itwill.lightbooks.dto;

import java.util.List;

import com.itwill.lightbooks.domain.Notification;

import lombok.Data;

@Data
public class NotificationFragmentDto {

	int count;
	List<Notification> notifications;
}
