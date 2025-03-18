package com.itwill.lightbooks.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NovelRatingResponse {
	private BigDecimal avgRating;
}	
