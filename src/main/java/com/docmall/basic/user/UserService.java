package com.docmall.basic.user;

import java.util.UUID;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserMapper userMapper;

	
	public void join(UserVO vo) {
		userMapper.join(vo);
		
	}

	
	public String idCheck(String mbsp_id) {

		return userMapper.idCheck(mbsp_id);
	}
	
	// 로그인
	public UserVO login(String mbsp_id) {
		
		return userMapper.login(mbsp_id);
	};
	
	// 아이디 찾기
	public String idFind(String mbsp_name, String mbsp_email) {
		
		return userMapper.idFind(mbsp_name, mbsp_email);
	};
	

	public String pwFind(String mbsp_id, String mbsp_name, String mbsp_email) {
		return userMapper.pwFind(mbsp_id, mbsp_name, mbsp_email);
	};

	// 임시 비밀번호 생성(UUID 이용)
	public String getTempPw() {
		String tempPw = UUID.randomUUID().toString().replaceAll("-", ""); // - 를 제거
		tempPw = tempPw.substring(0, 10); // 10자리
		return tempPw;
	}
	
	public void tempPwUpdate(String mbsp_id, String temp_enc_pw) {
		userMapper.tempPwUpdate(mbsp_id, temp_enc_pw);
	};
	
	public void modify(UserVO vo) {
		userMapper.modify(vo);
	};
	
	public void changePw(String mbsp_id, String new_pwd) {
		userMapper.changePw(mbsp_id, new_pwd);		
	};
	
	public void delete(String mbsp_id) {
		userMapper.delete(mbsp_id);		
	};
	
	public String existUserInfo(String sns_email) {
		return userMapper.existUserInfo(sns_email);
	}
	public String snsUserCheck(String sns_email) {
		return userMapper.snsUserCheck(sns_email);
	};
	
	public void snsUserInsert(SNSUserDto dto) {
		userMapper.snsUserInsert(dto);
	};
	
	
}
