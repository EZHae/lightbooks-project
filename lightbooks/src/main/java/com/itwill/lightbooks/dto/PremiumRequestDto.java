package com.itwill.lightbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PremiumRequestDto {

	private Long userId;
	private Long novelId;
	private Integer type;
	private Integer status;
	
}
