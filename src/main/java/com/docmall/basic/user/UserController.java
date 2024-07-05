package com.docmall.basic.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docmall.basic.kakaologin.KakaoUserInfo;
import com.docmall.basic.mail.EmailDTO;
import com.docmall.basic.mail.EmailService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user/*")
@Controller
public class UserController {

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	private final EmailService emailService;

	@GetMapping("join")
	public void joinForm() {
		log.info("join");

	}

	@PostMapping("join")
	public String joinOk(UserVO vo) throws Exception {

		log.info("회원정보 " + vo);

		// 비밀번호 암호화 변경
		vo.setMbsp_password(passwordEncoder.encode(vo.getMbsp_password()));

		userService.join(vo);

		return "redirect:/user/login";
	}

	@GetMapping("idCheck")
	public ResponseEntity<String> idCheck(String mbsp_id) throws Exception {

		log.info("아이디: " + mbsp_id);

		ResponseEntity<String> entity = null;

		String idUse = ""; // 클라이언트에서 dataType: text로 사용함

		if (userService.idCheck(mbsp_id) != null) {
			idUse = "no"; // 사용 불가능
		} else {
			idUse = "yes"; // 사용 가능
		}

		entity = new ResponseEntity<String>(idUse, HttpStatus.OK);

		return entity;

	}

	// 로그인 폼
	@GetMapping("/login")
	public void loginForm(UserVO vo) {
		log.info("로그인");
	}

	// 로그인 체크
	@PostMapping("/login") // 1.LoginDTO dto 2. String mbsp_id, String u_pwd
	public String loginOk(LoginDTO dto, HttpSession session, RedirectAttributes rttr) throws Exception {

		UserVO vo = userService.login(dto.getMbsp_id());

		String msg = "";
		String url = "/"; // "/" 메인 주소

		if (vo != null) {
			// 아이디가 존재하는 경우
			// 비밀번호 비교
			// dto : 사용자가 입력한 비밀번호, vo : db에 저장된 암호화 비밀번호
			// 사용자가 입력한 비밀번호가 암호화된 형태에 해당하는 것이라면
			if (passwordEncoder.matches(dto.getMbsp_password(), vo.getMbsp_password())) {

				// 암호화된 비밀번호를 노출 안시키기 위해
				vo.setMbsp_password("");

				// 로그인 여부를 확인 null 아니면 로그인
				session.setAttribute("loginStatus", vo);

			} else {
				// 사용자가 입력한 비밀번호가 암호화된 형태에 해당하지 않는 것이라면
				msg = "failPW";
				url = "/user/login"; // 로그인 폼 주소
				log.info("비밀번호 실패");
			}

		} else {
			// 아이디가 존재하지 않을 경우
			msg = "failID";
			url = "/user/login"; // 로그인 폼 주소
			log.info("아이디 실패");
		}

		// jsp에서 msg 변수를 사용 목적
		rttr.addFlashAttribute("msg", msg); // thymeleaf에서 msg 변수를 사용목적

		return "redirect:" + url; // 메인으로 이동
	}

	@GetMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate();

		return "redirect:/";
	}

	// 아이디 찾기
	@GetMapping("/idfind")
	public void idFindForm() {

	}

	@PostMapping("/idfind")
	public String idFindOk(String mbsp_name, String mbsp_email, String u_authcode, HttpSession session,
			RedirectAttributes rttr) throws Exception {

		log.info("이름: " + mbsp_name);
		log.info("이메일: " + mbsp_email);
		log.info("인증코드: " + u_authcode);

		String url = "";
		String msg = "";

		// 인증코드 확인 작업
		if (u_authcode.equals(session.getAttribute("authCode"))) {

			// 아이디를 찾아 메일 발송
			String mbsp_id = userService.idFind(mbsp_name, mbsp_email);

			// 이름과 메일 주소 확인
			if (mbsp_id != null) {

				// 찾은 아이디 메일 발송 작업
				String subject = "DocMall 아이디를 보냅니다.";
				EmailDTO dto = new EmailDTO("DocMall", "DocMall", mbsp_email, subject, mbsp_id);

				emailService.sendMail("emailIDResult", dto, mbsp_id);

				session.removeAttribute("authCode");

				msg = "successID";
				url = "/user/login";

				rttr.addFlashAttribute("msg", msg);

			} else {
				msg = "idFail";
				url = "/user/idfind";
				rttr.addFlashAttribute("msg", msg);
				rttr.addFlashAttribute("mbsp_name", mbsp_name);
				rttr.addFlashAttribute("mbsp_email", mbsp_email);

			}

		} else {
			msg = "failAuth";
			url = "/user/idfind";
			rttr.addFlashAttribute("msg", msg);
			rttr.addFlashAttribute("mbsp_name", mbsp_name);
			rttr.addFlashAttribute("mbsp_email", mbsp_email);

		}

		return "redirect:" + url;
	}

	@GetMapping("pwfind")
	public void pwFind() {

	}

	@PostMapping("pwfind")
	public String idFind(String mbsp_id, String mbsp_name, String mbsp_email, String u_authcode, HttpSession session,
			RedirectAttributes rttr) throws Exception {

		String url = "";
		String msg = "";

		// 인증코드 확인 작업
		if (u_authcode.equals(session.getAttribute("authCode"))) {
			// 사용자가 입력한 정보(3개 - 아이디, 이름, 이메일)를 조건으로 사용하여, 이메일을 db에서 가져온다.
			String d_email = userService.pwFind(mbsp_id, mbsp_name, mbsp_email);
			if (d_email != null) {

				// 비밀번호 임시코드를 생성하여, 암호화해서 사용자 아이디에 비밀번호를 수정한다.
				// 임시 비밀번호 생성(UUID 이용)
				String tempPw = userService.getTempPw();

				// 임시 비밀번호 생성 후 암호화
				String temp_enc_pw = passwordEncoder.encode(tempPw);

				// 암호화된 임시 비밀번호를 해당 아이디에 업데이트
				userService.tempPwUpdate(mbsp_id, temp_enc_pw);

				// 그리고, 임시코드를 메일로 발급한다.
				EmailDTO dto = new EmailDTO("DocMall", "DocMall", d_email, "DocMall에서 임시 비밀번호를 보냅니다.", tempPw);

				emailService.sendMail("emailPwResult", dto, tempPw);

				session.removeAttribute("authCode");

				url = "/user/login";
				msg = "successTempPw";
				rttr.addFlashAttribute("msg", msg);

			} else {

				url = "/user/pwfind";
				msg = "failInput";
				rttr.addFlashAttribute("msg", msg);
				rttr.addFlashAttribute("mbsp_id", mbsp_id);
				rttr.addFlashAttribute("mbsp_name", mbsp_name);
				rttr.addFlashAttribute("mbsp_email", mbsp_email);

			}

		} else {
			url = "/user/pwfind";
			msg = "failAuth";
			rttr.addFlashAttribute("msg", msg);
			rttr.addFlashAttribute("mbsp_id", mbsp_id);
			rttr.addFlashAttribute("mbsp_name", mbsp_name);
			rttr.addFlashAttribute("mbsp_email", mbsp_email);
		}

		return "redirect:" + url;
	}
	
	// 일반 로그인 또는 카카오 로그인인지를 체크하는 작업
	@GetMapping("mypage")
	public void mypage(HttpSession session, Model model) throws Exception {

		if(session.getAttribute("loginStatus") != null) {
			// 세션에 저장된 u_id 정보를 꺼내오는 방법
			// getAttribute(String name) Object -> UserInfoVO 형변환
			String u_id = ((UserVO) session.getAttribute("loginStatus")).getMbsp_id();

			UserVO vo = userService.login(u_id);

			model.addAttribute("user", vo);
		} else if(session.getAttribute("kakao_status") != null) {
			
			KakaoUserInfo kakaoUserInfo = (KakaoUserInfo) session.getAttribute("kakao_status"); 
			
			// mypage에서 보여줄 정보를 선택적으로 작업
			UserVO vo = new UserVO();
			vo.setMbsp_name(kakaoUserInfo.getNickname());
			vo.setMbsp_email(kakaoUserInfo.getEmail());
			
			model.addAttribute("user", vo);		
			model.addAttribute("msg","kakao_login");
		}
		

	}

	@PostMapping("modify")
	public String modify(UserVO vo, HttpSession session, RedirectAttributes rttr) throws Exception {

		// 이걸 메서드마다 코드를 넣어줘야하는데 해결하는 방법은 -> 인터셉터를 해야함
		if (session.getAttribute("loginStatus") == null)
			return "redirect:/user/login";

		String mbsp_id = ((UserVO) session.getAttribute("loginStatus")).getMbsp_id();

		log.info("수정데이터: " + vo);

		userService.modify(vo);

		rttr.addFlashAttribute("msg", "success");

		return "redirect:/user/mypage";
	}

	// 비밀번호 변경
	@GetMapping("changepw")
	public void changPw() {

	}

	// 비밀번호 변경
	@PostMapping("changepw")
	public String changPw(String cur_pwd, String new_pwd, HttpSession session, RedirectAttributes rttr) {

		String mbsp_id = ((UserVO) session.getAttribute("loginStatus")).getMbsp_id();

		UserVO vo = userService.login(mbsp_id);

		String msg = "";

		if (vo != null) {
			// 아이디가 존재하는 경우
			// 비밀번호 비교
			if (passwordEncoder.matches(cur_pwd, vo.getMbsp_password())) {
				// 신규 비밀번호 변경 작업
				String mbsp_password = passwordEncoder.encode(new_pwd);
				userService.changePw(mbsp_id, mbsp_password);
				msg = "success";
			} else {
				// 사용자가 입력한 비밀번호가 암호화된 형태에 해당하지 않는 것이라면
				msg = "failPW";

			}

		}

		// jsp에서 msg 변수를 사용 목적
		rttr.addFlashAttribute("msg", msg);

		return "redirect:/user/changepw";
	}

	@GetMapping("delete")
	public void delete() {

	}

	// 회원 탈퇴
	@PostMapping("delete")
	public String delete(String mbsp_password, HttpSession session, RedirectAttributes rttr) throws Exception {

		String mbsp_id = ((UserVO) session.getAttribute("loginStatus")).getMbsp_id();

		// 쿼리를 다시 만들어서 비밀번호를 하나만 가져오는 쿼리를 만들어야함 
		UserVO vo = userService.login(mbsp_id);

		String msg = "";
		String url = "";

		log.info("정보: " + vo);

		if (vo != null) {
			// 아이디가 존재하는 경우
			// 비밀번호 비교
			if (passwordEncoder.matches(mbsp_password, vo.getMbsp_password())) {
				userService.delete(mbsp_id);
				session.invalidate();
				msg = "successDelete";
				url = "/";
			} else {
				// 사용자가 입력한 비밀번호가 암호화된 형태에 해당하지 않는 것이라면
				msg = "fail";
				url = "/user/delete";

			}

		}

		// jsp에서 msg 변수를 사용 목적
		rttr.addFlashAttribute("msg", msg);

		return "redirect:" + url;
	}

}
