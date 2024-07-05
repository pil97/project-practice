package com.docmall.basic.user;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.kakaologin.KakaoUserInfo;

public interface UserMapper {

	// 회원가입
	void join(UserVO vo);

	// 아이디 중복체크
	// 1번째 타입 String -> resultType
	// 매개변수 타입 String -> parameterType
	String idCheck(String mbsp_id);

	// 로그인
	UserVO login(String mbsp_id);

	// 아이디 찾기
	String idFind(@Param("mbsp_name") String mbsp_name, @Param("mbsp_email") String mbsp_email);

	// 비밀번호 재발급 확인 작업
	String pwFind(@Param("mbsp_id") String mbsp_id, @Param("mbsp_name") String mbsp_name, @Param("mbsp_email") String mbsp_email);

	// 임시 비밀번호 생성 후 DB 수정
	void tempPwUpdate(@Param("mbsp_id") String mbsp_id, @Param("temp_enc_pw") String temp_enc_pw);
	
	// 내정보 수정
	void modify(UserVO vo);

	// 비밀번호 변경
	void changePw(@Param("mbsp_id")  String mbsp_id, @Param("new_pwd") String new_pwd);
	
	// 회원탈퇴 
	void delete(String mbsp_id);
	
	String existUserInfo(String sns_email);
	
	// sns 유저 중복 체크
	String snsUserCheck(String sns_email);
	
	void snsUserInsert(SNSUserDto dto);
	
	
	
}
