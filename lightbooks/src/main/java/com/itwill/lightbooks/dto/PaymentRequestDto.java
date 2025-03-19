package com.itwill.lightbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

	private Long userId;
	private String itemName;
	private Long coin;
	private Long cash;
	private Long mileage;
	private String descrip;
	private int type;
	private int grade;
}
