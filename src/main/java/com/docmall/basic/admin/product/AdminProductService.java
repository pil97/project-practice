package com.docmall.basic.admin.product;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.docmall.basic.common.dto.Criteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminProductService {

	private final AdminProductMapper adminProductMapper;
	
	public void productInsert(AdminProductVO vo) {
		adminProductMapper.productInsert(vo);
	};
	
	// 상품 리스트
	public List<AdminProductVO> productList(Criteria cri) {
		return adminProductMapper.productList(cri);
	};
	
	// 상품 리스트 개수
	public int getTotalCount(Criteria cri) {
		return adminProductMapper.getTotalCount(cri);
	};
	
	// 상품 수정
	public AdminProductVO productEditPage(Integer pro_num) {
		return adminProductMapper.productEditPage(pro_num);
	};
	
	// 상품 수정
	public void productEdit(AdminProductVO vo) {
		adminProductMapper.productEdit(vo);
	};
	
	// 상품 삭제 
	public void productDelete(Integer pro_num) {
		adminProductMapper.productDelete(pro_num);
	};
	
	// 체크상품 수정작업1
	// 체크된 개수만큼 반복문이 동작이 되어, 커넥션 객체수가 진행이 되기 때문에 성능적으로는 권장할 사항은 아니다
	// 사용자에서 요청이 되는 작업인 경우, 많은 사용자로 인한 동시 작업에는 성능이 안좋다
	// 관리자에서 요청이 되는 작업인 경우, 문제되지 않는다.
	public void productCheckedModify1(List<Integer> pro_num_arr, List<Integer> pro_price_arr, List<String> pro_buy_arr) {
		
		for(int i = 0; i < pro_num_arr.size(); i++) {
			adminProductMapper.productCheckedModify1(pro_num_arr.get(i), pro_price_arr.get(i), pro_buy_arr.get(i));			
		}		
	};
	
	// 체크상품 수정작업2
	public void productCheckedModify2(List<Integer> pro_num_arr, List<Integer> pro_price_arr, List<String> pro_buy_arr) {
		List<ProductDTO> pro_modify_list = new ArrayList<>();
		
		for(int i = 0; i < pro_num_arr.size(); i++) {
			ProductDTO productDTO = new ProductDTO(pro_num_arr.get(i), pro_price_arr.get(i), pro_buy_arr.get(i));
			pro_modify_list.add(productDTO);
		}
		
		adminProductMapper.productCheckedModify2(pro_modify_list);
					
	};
}
