package com.itwill.lightbooks.dto;

import java.time.LocalDateTime;

import com.itwill.lightbooks.domain.Ticket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketReadDto {

	private String grade;
	private String novelTitle;
	private LocalDateTime createdTime;
	
	public static TicketReadDto fromEntity(Ticket ticket) {
		if (ticket != null) {
			return TicketReadDto.builder()
					.grade((ticket.getGrade() == 0) ? "전체 이용권" : "작품 이용권")
					.novelTitle((ticket.getNovel() == null) ? "-" : ticket.getNovel().getTitle())
					.createdTime(ticket.getCreatedTime())
					.build();
		} else {
			return null;
		}
	}
}
