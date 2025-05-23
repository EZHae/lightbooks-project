package com.itwill.lightbooks.repository.novel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.lightbooks.domain.NGenre;

public interface NGenreRepository extends JpaRepository<NGenre, Long> {

	@Modifying
    @Transactional
    @Query("DELETE FROM NGenre ng WHERE ng.novel.id = :novelId")
	Integer deleteByNovelId(Long novelId);

}
