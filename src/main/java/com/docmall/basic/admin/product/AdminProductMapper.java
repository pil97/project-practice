package com.docmall.basic.admin.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.common.dto.Criteria;

public interface AdminProductMapper {
	
	// 상품 등록 
	void productInsert(AdminProductVO vo);
	
	// 상품 리스트
	List<AdminProductVO> productList(Criteria cri);
	
	// 상품 리스트 개수
	int getTotalCount(Criteria cri);
	
	// 상품 수정 페이지 정보
	AdminProductVO productEditPage(Integer pro_num);
	
	// 상품 수정
	void productEdit(AdminProductVO vo);
	
	// 상품 삭제 
	void productDelete(Integer pro_num);
	
	// 체크상품 수정작업1
	void productCheckedModify1(@Param("pro_num") Integer pro_num, @Param("pro_price") Integer pro_price, @Param("pro_buy") String pro_buy);
	
	// 체크상품 수정작업2
	void productCheckedModify2(List<ProductDTO> pro_modify_list);
}
