package com.itwill.lightbooks.repository.episode;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Long>{
	
}