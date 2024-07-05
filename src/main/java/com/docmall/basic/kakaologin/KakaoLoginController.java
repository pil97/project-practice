package com.docmall.basic.kakaologin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.user.SNSUserDto;
import com.docmall.basic.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/oauth2/*")
@RequiredArgsConstructor
public class KakaoLoginController {

	private final KakaoLoginService kakaoLoginService;
	
	private final UserService userService;

	@Value("${kakako.client.id}")
	private String clientId;

	@Value("${kakako.redirect.uri}")
	private String redirectUri;

	@Value("${kakako.client.secret}")
	private String clientSecret;

	// https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
	// Step 1-1단계 - 인가 코드 받기
	// 카카오 로그인 API Server에게 인가코드를 요청하는 작업 
	// 헤더는 없고, 요청 파라미터가 있는 경우 
	@GetMapping("/kakaologin")
	public String connect() {

		// 자기가 생성했던 메모리에 계속 저장 -> 메모리 성능이 더 좋음 String 보다
		StringBuffer url = new StringBuffer();
		url.append("https://kauth.kakao.com/oauth/authorize?");
		url.append("response_type=code");
		url.append("&client_id=" + clientId);
		url.append("&redirect_uri=" + redirectUri);
		// 추가옵션. 다시 사요자 인증을 수행하고자 할 때 사용
		url.append("&prompt=login");
		
		
		log.info("인가코드 : " + url.toString());

		return "redirect:" + url.toString();
	}

	// Step 1-2단계 - 카카오 로그인 API Server에서 개발사이트 callback 주소 호출 카카오 개발자 사이트에서 애플리케이션 등록과정에서 아래주소를 설정을 이미 한 상태
	@GetMapping("/callback/kakao")
	public String callback(String code, HttpSession session) {

		log.info("code : " + code);

		String accessToken = "";
		KakaoUserInfo kakaoUserInfo = null;

		try {
			// 카카오 로그인 API 서버에게 로그인 인증을 성공
			accessToken = kakaoLoginService.getAccessToken(code); // 인가코드를 통한 인증토큰을 요청 
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		try {
			// 카카오 로그인 API 서버에서 보내 온 사용자 정보
			kakaoUserInfo = kakaoLoginService.getKakaoUserInfo(accessToken); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log.info("access : " + accessToken);
		
		if (kakaoUserInfo != null) {

			log.info("사용자정보: " + kakaoUserInfo);

			/* db작업
			 * 
			 * 카카오 로그인 사용자가 회원테이블에 존재 유무 
			 * 1. 존재 : 회원수정 작업 - 회원테이블에서 가져오면 됨
			 * 2. 존재 x : 회원수정작업 - 회원테이블 참조안하고, 해당 클래스를 에러없도록 작업한다.
			 * 카카오 회원테이블 존재 유무
			 * 
			 */
			

			// 인증을 세션 방식으로 처리 
			session.setAttribute("kakao_status", kakaoUserInfo);	// 인증여부 사용
			session.setAttribute("accessToken", accessToken);		// 카카오 로그아웃 사용
			
			String sns_email = kakaoUserInfo.getEmail();
			
			String sns_login_type = userService.existUserInfo(sns_email);
			// session.setAttribute("sns_type", sns_login_type);
									
			// 둘 다 데이터가 존재하지 않으면 - 회원테이블 존재 안하고, 카카오 테이블에도 존재 안하면
			// && : 같은 데이터 테스트시, 1번째는 좌측은 true, 우측도 true가 되어 데이터가 삽입	 - 전체조건이 true
			//						두번째는 좌측은 true, 우측은 false가 되어 insert가 실행 안됨 - 전체조건이 false 
			if(userService.existUserInfo(sns_email) == null && userService.snsUserCheck(sns_email) == null) {
				// SNS 테이블 데이터 삽입 작업
				SNSUserDto dto = new SNSUserDto();
				dto.setId(kakaoUserInfo.getId().toString());
				dto.setEmail(kakaoUserInfo.getEmail());
				dto.setNickname(kakaoUserInfo.getNickname());
				dto.setSns_type("kakao");
				
				userService.snsUserInsert(dto);

			}
		}

		return "redirect:/";
	}

	@GetMapping("/kakaologout")
	public String kakaoLogout(HttpSession session) {
						
		String accessToken = (String) session.getAttribute("accessToken");
				
		log.info("access : " + accessToken);
		
		if(accessToken != null && !"".equals(accessToken)) {
			try {
				log.info("test");
				kakaoLoginService.kakaoLogout(accessToken);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);				
			}
			
			session.removeAttribute("kakao_status");
			session.removeAttribute("accessToken");
		}
		
		log.info("로그아웃");
		
		return "redirect:/";
	}
	
}
