package com.itwill.lightbooks.repository.coinpayment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.lightbooks.domain.CoinPayment;

public interface CoinPaymentRepository extends JpaRepository<CoinPayment, Long> {

	Page<CoinPayment> findByUserIdAndType(Long userId, int type, Pageable pageable);
	
	// 후원한 유저들 랭킹 조회
    @Query(value = "SELECT u.nickname AS nickname, SUM(cp.coin) AS totalDonation " +
            "FROM CoinPayment cp " +
            "JOIN User u ON u.id = cp.userId " +
            "WHERE cp.type = 3 AND cp.novel.id = :novelId " +
            "GROUP BY u.nickname " +
            "ORDER BY totalDonation ASC " + 
            "LIMIT 10")
    List<Object[]> findDonationRankingByNovelId(@Param("novelId") Long novelId);
	
    Page<CoinPayment> findByNovelIdAndType(Long novelId, int type, Pageable pageable);
}
