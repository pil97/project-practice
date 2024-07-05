package com.docmall.basic.admin.category;

import java.util.List;

public interface AdminCategoryMapper {

	// 1차 카테고리 목록
	List<AdminCategoryVO> getFirstCategoryList();
	
	// 2차 카테고리
	List<AdminCategoryVO>getSecondCategoryList(int cat_prtcode);
	
	// 2차 카테고리 정보를 이용한 1차 카테고리 정보
	AdminCategoryVO getFirstCategoryBySecondCategory(int cat_code);
}
