package com.docmall.basic.cart;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CartVO {

	private Long cart_code;
	private int pro_num;
	private String mbsp_id;
	private int cart_amount;
	private Date cart_date;	// Calendar, LocalDate, LocalTime, LocalDateTime	
	
	
}
