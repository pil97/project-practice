package com.docmall.basic.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.admin.product.AdminProductVO;
import com.docmall.basic.common.dto.Criteria;
import com.docmall.basic.common.dto.PageDTO;
import com.docmall.basic.common.util.FileManagerUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/product/*")
@Controller
public class ProductController {

	private final ProductService productService;
	
	// 상품 이미지 업로드 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;

	@GetMapping("/productlist")
	public void productList(@ModelAttribute("cat_code") int cat_code, @ModelAttribute("cat_name") String cat_name, Criteria cri, Model model)
			throws Exception {

		cri.setAmount(9);
		
		log.info("2차 카테고리 코드 : " + cat_code);
		log.info("2차 카테고리 이름 : " + cat_name);

		List<AdminProductVO> userProductList = productService.productList(cat_code, cri);

		int totalCount = productService.getCountProductByCategory(cat_code);

		model.addAttribute("userProductList", userProductList);
		model.addAttribute("pageMaker", new PageDTO(cri, totalCount));

	}

	// 상품리스트 - 이미지 보여주기 <img src="메핑주소">
	@GetMapping("/imagedisplay")
	public ResponseEntity<byte[]> imageDisplay(String dateFolderName, String fileName) throws Exception {

//			log.info("이미지 업로드 경로 : " + uploadPath);
//			log.info("이미지 업로드 경로 : " + dateFolderName);
//			log.info("이미지 업로드 경로 : " + fileName );

		return FileManagerUtils.getFile(uploadPath + dateFolderName, fileName);
	}
	
	// 상품정보 - 모달창
	@GetMapping("/productinfo")
	public ResponseEntity<AdminProductVO> productInfo(int pro_num) throws Exception{
		
		ResponseEntity<AdminProductVO> entity = null;
		
		// jsp, tyhmeleaf에서 보여줄려고 하면 model 작업
		// 그냥 정보만 넘겨줄때는 ResponseEntity 작업
		
		// db 연동
		AdminProductVO vo = productService.productInfo(pro_num);
		// vo.setPro_img(vo.getPro_up_folder().replace("\\", "/"));
		
		
		entity = new ResponseEntity<AdminProductVO>(vo, HttpStatus.OK);
		
		
		
		return entity;
		
	}

}
