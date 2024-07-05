package com.docmall.basic.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.admin.product.AdminProductVO;
import com.docmall.basic.common.dto.Criteria;

public interface ProductMapper {

	List<AdminProductVO> productList(@Param("cat_code") int cat_code ,@Param("cri") Criteria cri);
	
	int getCountProductByCategory(int cat_code);
	
	AdminProductVO productInfo(int pro_num); 
	
}
