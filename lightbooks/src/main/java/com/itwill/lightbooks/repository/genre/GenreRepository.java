package com.itwill.lightbooks.repository.genre;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.lightbooks.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

}
