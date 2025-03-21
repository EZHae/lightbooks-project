package com.itwill.lightbooks.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.Ticket;
import com.itwill.lightbooks.dto.EpisodeBuyDto;
import com.itwill.lightbooks.domain.User;
import com.itwill.lightbooks.repository.ticket.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
	
	private final TicketRepository ticketRepo;
	
	//수정
	public Long getNovelTicketCount(Authentication authentication, Long novelId) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return ticketRepo.findNovelTicketCount(userId, novelId);
    }

	//수정
    public Long getGlobalTicketCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return ticketRepo.findGlobalTicketCount(userId);
    }

//    // 특정 작품 무료 이용권 사용
//    public void useNovelTicket(Authentication authentication, Long novelId, Integer grade) {
//        User user = (User) authentication.getPrincipal();
//        Long userId = user.getId();
//
//        Ticket ticket = ticketRepo.findTopByUserIdAndNovelIdAndGrade(userId, novelId, grade);
//
//        if (ticket == null) {
//            throw new IllegalStateException("해당 작품에 사용할 무료 이용권이 없습니다.");
//        }
//
//        // 무료 이용권 사용 처리
//        ticketRepo.delete(ticket);
//    }
//
//    // 공용 무료 이용권 사용
//    public void useGlobalTicket(Authentication authentication, Integer grade) {
//        User user = (User) authentication.getPrincipal();
//        Long userId = user.getId();
//
//        Ticket ticket = ticketRepo.findTopByUserIdAndGrade(userId, grade);
//
//        if (ticket == null) {
//            throw new IllegalStateException("공용 무료 이용권이 없습니다.");
//        }
//
//        // 무료 이용권 사용 처리
//        ticketRepo.delete(ticket);
//    }
    
    // 이용권으로 회차 구매 시 가장 오래된 이용권 삭제
    @Transactional
    public Long deleteTicketFromEpisodeBuyDto(EpisodeBuyDto dto) {
    	int grade = dto.getType(); // 우연치 않게 dto.getType이 0이면 grade도 글로벌이라 사용하기 좋음
    	Long ticketId;
    	if (grade == 0) {
    		ticketId = ticketRepo.findOldestGlobalTicketId(dto.getUserId(), grade);
    		ticketRepo.deleteById(ticketId);
    	} else if (grade == 1) {
    		ticketId = ticketRepo.findOldestNovelTicketId(dto.getUserId(), dto.getNovelId(), grade);
    		ticketRepo.deleteById(ticketId);
    	} else {
    		return 0L;
    	}
    	return ticketId;
    }
    
}
