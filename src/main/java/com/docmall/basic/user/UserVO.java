package com.docmall.basic.user;

import java.util.Date;

import lombok.Data;

/*
성능관점
	- 회원테이블 (로그인 포함)
	- 회원테이블 + 로그인테이블
*/	 

@Data
public class UserVO {
	private String mbsp_id;
	private String mbsp_name;
	private String mbsp_email;
	private String mbsp_password;
	private String mbsp_zipcode;
	private String mbsp_addr;
	private String mbsp_deaddr;
	private String mbsp_phone;
	private String mbsp_nick;
	private String mbsp_receive;
	private int mbsp_point;	
	private Date mbsp_lastlogin;
	private Date mbsp_datesub;
	private Date mbsp_updatedate;
	private String sns_login_type;
}
