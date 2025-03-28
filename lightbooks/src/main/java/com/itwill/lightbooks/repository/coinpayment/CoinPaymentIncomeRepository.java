package com.itwill.lightbooks.repository.coinpayment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.lightbooks.domain.CoinPaymentIncome;

public interface CoinPaymentIncomeRepository extends JpaRepository<CoinPaymentIncome, Long> {

	// 소설의 수익모델 별 누계액 조회
	@Query(value = "SELECT n.id, n.title, n.grade, n.cover_src AS coverSrc, " +
            "SUM(CASE WHEN cpi.type = 4 THEN cpi.coin ELSE 0 END) AS type4Income, " +
            "SUM(CASE WHEN cpi.type = 5 THEN cpi.coin ELSE 0 END) AS type5Income " +
            "FROM NOVELS n " +
            "JOIN COIN_PAYMENT_INCOME cpi ON n.id = cpi.novel_id " +
            "WHERE n.user_id = :userId " +
            "GROUP BY n.id, n.title, n.grade, n.cover_src " +
            "ORDER BY n.id ASC", 
    countQuery = "SELECT COUNT(DISTINCT n.id) FROM NOVELS n " +  // total count query (required for pageable)
                 "JOIN COIN_PAYMENT_INCOME cpi ON n.id = cpi.novel_id " +
                 "WHERE n.user_id = :userId", 
    nativeQuery = true)
	Page<Object[]> findByUserId(@Param("userId") Long userId, Pageable pageable);
	
	Page<CoinPaymentIncome> findByNovelIdAndType(Long novelId, int type, Pageable pageable);
}
