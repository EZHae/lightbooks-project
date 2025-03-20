package com.itwill.lightbooks.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Ticket;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.repository.ticket.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
	
	private TicketRepository ticketRepo;
	
	// 특정 유저가 가진 특정 작품 무료 이용권 갯수 조회
    public int getNovelTicketCount(Authentication authentication, Long novelId) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return ticketRepo.findNovelTicketCount(userId, novelId);
    }

    // 특정 유저가 가진 모든 작품 무료 이용권 갯수 조회
    public int getGlobalTicketCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return ticketRepo.findGlobalTicketCount(userId);
    }

    // 특정 작품 무료 이용권 사용
    public void useNovelTicket(Authentication authentication, Long novelId, Integer grade) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        Ticket ticket = ticketRepo.findTopByUserIdAndNovelIdAndGrade(userId, novelId, grade);

        if (ticket == null) {
            throw new IllegalStateException("해당 작품에 사용할 무료 이용권이 없습니다.");
        }

        // 무료 이용권 사용 처리
        ticketRepo.delete(ticket);
    }

    // 공용 무료 이용권 사용
    public void useGlobalTicket(Authentication authentication, Integer grade) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        Ticket ticket = ticketRepo.findTopByUserIdAndGrade(userId, grade);

        if (ticket == null) {
            throw new IllegalStateException("공용 무료 이용권이 없습니다.");
        }

        // 무료 이용권 사용 처리
        ticketRepo.delete(ticket);
    }
    
}
