package com.docmall.basic.admin;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminMapper adminMapper;
	
	public AdminVO loginOk(String admin_id) {
		return adminMapper.loginOk(admin_id);
	};
}
