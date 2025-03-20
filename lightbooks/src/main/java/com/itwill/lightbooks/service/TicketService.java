package com.itwill.lightbooks.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Ticket;
import com.itwill.lightbooks.repository.ticket.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
	
	private TicketRepository ticketRepo;
	
	// 특정 작품 무료 이용권 갯수 조회
	@Transactional(readOnly = true)
    public int getNovelTicketCount(Long userId, Long novelId) {
        return ticketRepo.countNovelTickets(userId, novelId);
    }

    // 모든 작품 무료 이용권 갯수 조회
    @Transactional(readOnly = true)
    public int getGlobalTicketCount(Long userId) {
        return ticketRepo.countGlobalTickets(userId);
    }
    
    // 특정 작품 무료 이용권 사용
    @Transactional
    public void useNovelTicket(Long userId, Long novelId) {
        // 티켓 갯수 확인
        int availableTickets = getNovelTicketCount(userId, novelId);
        if (availableTickets <= 0) {
            throw new IllegalStateException("해당 작품 무료 이용권이 없습니다.");
        }

        // 티켓 사용 로직 (사용 후 삭제한다고 가정)
        Ticket ticket = ticketRepo.findTopByUserIdAndNovelIdAndGrade(userId, novelId, 1);
        if (ticket != null) {
            ticketRepo.delete(ticket); // 무료 이용권 사용 처리
        }
    }

    // 모든 작품에 사용가능한 무료 이용권 사용
    @Transactional
    public void useGlobalTicket(Long userId) {
        // 티켓 갯수 확인
        int availableTickets = getGlobalTicketCount(userId);
        if (availableTickets <= 0) {
            throw new IllegalStateException("모든 작품 무료 이용권이 없습니다.");
        }

        // 공용 티켓 사용 로직 (사용 후 삭제한다고 가정)
        Ticket ticket = ticketRepo.findTopByUserIdAndGrade(userId, 0);
        if (ticket != null) {
            ticketRepo.delete(ticket); // 무료 이용권 사용 처리
        }
    }
    
}
