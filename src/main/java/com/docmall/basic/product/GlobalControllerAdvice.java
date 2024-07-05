package com.docmall.basic.product;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.docmall.basic.admin.category.AdminCategoryService;
import com.docmall.basic.admin.category.AdminCategoryVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// com.docmall.basic : 기본 패키지를 설정하면, 전체 영향을 받는다

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice(basePackages = {"com.docmall.basic.product"})	// 카테고리 사용되는 컨트롤러의 패키지를 설정
public class GlobalControllerAdvice {
	
	private final AdminCategoryService adminCategoryService;
	
	@ModelAttribute
	public void comnTest(Model model) {
		
		List<AdminCategoryVO> cateList = adminCategoryService.getFirstCategoryList();
		
		model.addAttribute("userCateList", cateList);
		
		log.info("공통코드 실행");
	}
	
}
