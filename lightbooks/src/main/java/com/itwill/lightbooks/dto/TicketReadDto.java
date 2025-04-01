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
        if (ticket == null) {
            return null;
        }

        String grade = (ticket.getGrade() == 0) ? "전체 이용권" : "작품 이용권";
        String novelTitle;

        if (ticket.getNovel() == null) {
            novelTitle = (ticket.getGrade() == 0) ? "-" : "작품이 삭제되어 전체 이용권으로 사용 가능한 이용권";
        } else {
            novelTitle = ticket.getNovel().getTitle();
        }

        return TicketReadDto.builder()
                .grade(grade)
                .novelTitle(novelTitle)
                .createdTime(ticket.getCreatedTime())
                .build();
    }
}