package com.docmall.basic.admin;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/admin/*")
@RequiredArgsConstructor
@Slf4j
@Controller
public class AdminController {
	private final AdminService adminService;
	
	private final PasswordEncoder passwordEncoder;
	
	@GetMapping("")
	public String loginForm() {
		
		return "admin/adlogin";
		
	}
	
	@PostMapping("/adminok")
	public String loginOk(AdminVO vo, HttpSession session) throws Exception{
		
		log.info("관리자 정보 : " + vo);
		log.info(vo.getAdmin_pw());
		
		AdminVO dVo = adminService.loginOk(vo.getAdmin_id());
		
		log.info("암호화 비밀번호 : " + dVo.getAdmin_pw());
		
		String url = "";
		
		if(dVo != null) {
			if(passwordEncoder.matches(vo.getAdmin_pw(), dVo.getAdmin_pw())) {
				log.info(dVo.getAdmin_pw());
				dVo.setAdmin_pw("");
				session.setAttribute("adminStatus", dVo);
				url = "admin/adminmenu";
			}
		}
		
		return "redirect:/" + url;
	}
	
	@GetMapping("/adminmenu")
	public void amdinMenu() {
		
	}
	
	@GetMapping("/logout")
	public String adminLogout(HttpSession session){
		
		session.removeAttribute("adminStatus");
		
		return "redirect:/admin/";
	}
}
