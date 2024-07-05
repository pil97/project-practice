package com.docmall.basic.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.docmall.basic.admin.product.AdminProductMapper;
import com.docmall.basic.admin.product.AdminProductVO;
import com.docmall.basic.common.dto.Criteria;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductMapper productMapper;
	
	public List<AdminProductVO> productList(int cat_code , Criteria cri) {
		return productMapper.productList(cat_code, cri);
	};
	
	public int getCountProductByCategory(int cat_code) {
		return productMapper.getCountProductByCategory(cat_code);				
	};
	
	public AdminProductVO productInfo(int pro_num) {
		return productMapper.productInfo(pro_num);
	}; 
	
}
