package com.itwill.lightbooks.repository.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.lightbooks.domain.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>{

	// 특정 유저가 가진 특정 작품 무료 이용권 갯수 조회
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.user.id = :userId AND t.novel.id = :novelId AND t.grade = 1")
    int countNovelTickets(@Param("userId") Long userId, @Param("novelId") Long novelId);

    // 특정 유저가 가진 모든 작품 무료 이용권 갯수 조회
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.user.id = :userId AND t.grade = 0")
    int countGlobalTickets(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Ticket t WHERE t.user.id = :userId AND t.novel.id = :novelId AND t.grade = :grade")
    Ticket findTopByUserIdAndNovelIdAndGrade(@Param("userId") Long userId, @Param("novelId") Long novelId, @Param("grade") Integer grade);

    @Query("SELECT t FROM Ticket t WHERE t.user.id = :userId AND t.grade = :grade")
    Ticket findTopByUserIdAndGrade(@Param("userId") Long userId, @Param("grade") Integer grade);

    // 글로벌 티켓 중 가장 오래된 데이터의 id 반환
    @Query(value = "SELECT id FROM TICKETS WHERE user_id = :userId AND grade = :grade ORDER BY created_time ASC LIMIT 1", nativeQuery = true)
    Long findOldestGlobalTicketId(@Param("userId") Long userId, @Param("grade") int grade);
    
    // 노벨 티켓중 가장 오래된 데이터의 id 반환
    @Query(value = "SELECT id FROM TICKETS WHERE user_id = :userId AND novel_id = :novelId AND grade = :grade ORDER BY created_time ASC LIMIT 1", nativeQuery = true)
    Long findOldestNovelTicketId(@Param("userId") Long userId, @Param("novelId") Long novelId, @Param("grade") int grade);
    
}
