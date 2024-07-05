package com.docmall.basic.admin.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/admin/category/*")
@RequiredArgsConstructor
@Slf4j
@Controller
public class AdminCategoryController {
	
	private final AdminCategoryService adminCategoryservice;
		
	// 2차 카테고리 목록 
	@GetMapping("/secondcategory/{cat_prtcode}")
	public ResponseEntity<List<AdminCategoryVO>> getSecondCategoryList(@PathVariable("cat_prtcode") int cat_prtcode) throws Exception{
		
		log.info("1차 카테고리 코드 : " + cat_prtcode);
		
		ResponseEntity<List<AdminCategoryVO>> entity = null;
		
		entity = new ResponseEntity<List<AdminCategoryVO>>(adminCategoryservice.getSecondCategoryList(cat_prtcode), HttpStatus.OK);
		
		return entity;
	}
	


}
