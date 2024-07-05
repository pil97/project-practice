package com.docmall.basic.admin.product;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docmall.basic.admin.category.AdminCategoryService;
import com.docmall.basic.admin.category.AdminCategoryVO;
import com.docmall.basic.common.dto.Criteria;
import com.docmall.basic.common.dto.PageDTO;
import com.docmall.basic.common.util.FileManagerUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/product/*")
@Controller
public class AdminProductController {
	private final AdminProductService adminProductService;

	private final AdminCategoryService adminCategoryService;

	// 상품 이미지 업로드 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;

	// CKeditor 파일업로드 경로
	@Value("${file.ckdir}")
	private String uploadCKPath;

	// 상품등록 폼
	@GetMapping("/productinsert")
	public void productInsert(Model model) {
		List<AdminCategoryVO> cateList = adminCategoryService.getFirstCategoryList();
		model.addAttribute("cateList", cateList);

	}

	// 상품등록 업로드
	// <input type="file" class="form-control" name="uploadFile" id="uploadFile">
	// 파라미터명 = name이랑 일치
	@PostMapping("/productinsert")
	public String productInsert(AdminProductVO vo, MultipartFile uploadFile) throws Exception {

		// 1. 상품이미지 업로드
		String dateFolder = FileManagerUtils.getDateFolder();
		String saveFileName = FileManagerUtils.uploadFile(uploadPath, dateFolder, uploadFile);

		vo.setPro_img(saveFileName);
		vo.setPro_up_folder(dateFolder);

		log.info("상품정보 : " + vo);

		// 2. 상품정보 DB저장
		adminProductService.productInsert(vo);

		return "redirect:/admin/product/productlist";
	}

	// CKEditor 상품설명 이미지 업로드
	// 파라미터명 - upload : 에디터 이미지 업로드 클릭시 나오는 name 명
	@PostMapping("/imageupload")
	public void imageUpload(HttpServletRequest request, HttpServletResponse response, MultipartFile upload) {

		OutputStream out = null;
		PrintWriter printWriter = null; // 서버에서 클라이언트에게 응답정보를 보낼때 사용 (업로드한 이미지 정보를 브라우저에게 보내는 작업용도)

		try {
			// 1. CKeditor를 이용한 파일업로드 처리
			String fileName = upload.getOriginalFilename(); // 업로드 할 클라이언트 파일 이름
			byte[] bytes = upload.getBytes(); // 업로드 할 파일의 바이트 배열

			// log.info("파일 이름: " + fileName);

			String ckUploadPath = uploadCKPath + fileName; // /Users/apple/Desktop/springBoot/spring_work/upload/ckeditor/
															// + "abc.gif"

			out = new FileOutputStream(ckUploadPath); // /Users/apple/Desktop/springBoot/spring_work/upload/ckeditor/abc.gif
														// 파일생성. 0byte

			out.write(bytes); // 빨대(스트림)의 공간에 업로드 할 파일의 바이트 배열을 채운 상태
			out.flush(); // /Users/apple/Desktop/springBoot/spring_work/upload/ckeditor/abc.gif 0 byte ->
							// 크기가 채워진 정상적인 파일로 인식

			// 2. 업로드한 이미지 파일에 대한 정보를 클라이언트에게 보내는 작업

			printWriter = response.getWriter();

			String fileUrl = "/admin/product/display/" + fileName; // 메핑주소/이미지파일
			// String fileUrl = fileName;

			log.info("파일 URL: " + fileUrl);

			// CKeditor 4.12에서는 파일 정보를 아래와 같이 구성해서 보내야함
			// ("filename :" + "abc.gif"(변수처리), "uploaded":1,
			// "url":"/ckupload/abc.gif"(변수처리)}
			// ("filename" :변수, "uploaded":1, "url":변수}
			// ckeditor 버전
			printWriter.println("{\"filename\" :\"" + fileName + "\", \"uploaded\":1, \"url\":\"" + fileUrl + "\"}"); // 스트림에
																														// 채움

			printWriter.flush();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (printWriter != null) {
				printWriter.close();
			}
		}

	}

	// <img src="매핑주소">
	@GetMapping("display/{fileName}")
	public ResponseEntity<byte[]> getFile(@PathVariable("fileName") String fileName) {

		ResponseEntity<byte[]> entity = null;

		log.info("파일이미지" + fileName);

		try {
			entity = FileManagerUtils.getFile(uploadCKPath, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return entity;
	}

	// 상품리스트
	@GetMapping("/productlist")
	public void productList(Criteria cri, Model model) throws Exception {

		// pageNum = 1, amount = 10

		// cri.setAmount(2);

		log.info("Criteria" + cri);

		List<AdminProductVO> productList = adminProductService.productList(cri);

		int totalCount = adminProductService.getTotalCount(cri);

		/*
		 * db 날짜 데이터가 역슬래시로 나오는 경우, 이 작업을 해줘야함 서버에는 역슬래시가 인식되지만, 브라우저에서는 인식안됨
		 * productList.forEach(vo -> {
		 * vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/")); });
		 */

		model.addAttribute("productList", productList);
		model.addAttribute("pageMaker", new PageDTO(cri, totalCount));
	}

	// 상품리스트 - 이미지 보여주기 <img src="메핑주소">
	@GetMapping("/imagedisplay")
	public ResponseEntity<byte[]> imageDisplay(String dateFolderName, String fileName) throws Exception {

//		log.info("이미지 업로드 경로 : " + uploadPath);
//		log.info("이미지 업로드 경로 : " + dateFolderName);
//		log.info("이미지 업로드 경로 : " + fileName );

		return FileManagerUtils.getFile(uploadPath + dateFolderName, fileName);
	}

	// 상품 수정 폼
	@GetMapping("/productedit")
	public void productEditPage(@ModelAttribute("cri") Criteria cri, Integer pro_num, Model model) throws Exception {

		// 1차 카테고리 목록
		List<AdminCategoryVO> cateList = adminCategoryService.getFirstCategoryList();
		model.addAttribute("cateList", cateList);

		// 상품정보(2차 카테고리)
		// model 이름 : 메서드의 리턴타입 AdminProductVO -> adminProductVO
		AdminProductVO vo = adminProductService.productEditPage(pro_num);

		// 클라이언트에 \를 /로 변환하여 model 작업전에 처리함, 2024\07\01 -> 2024/07/01
		// vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
		model.addAttribute("productVO", vo);

		// 1차 카테고리 정보
		int cat_code = vo.getCat_code(); // 상품테이블에 존재하는 2차 카테고리 코드
		int cat_prtcode = adminCategoryService.getFirstCategoryBySecondCategory(cat_code).getCat_prtcode();
		model.addAttribute("cat_prtcode", cat_prtcode);

		// 2차 카테고리 정보
		model.addAttribute("subCateList", adminCategoryService.getSecondCategoryList(cat_prtcode));

	}

	// 상품 수정
	@PostMapping("/productedit")
	public String productEdit(AdminProductVO vo, MultipartFile uploadFile, Criteria cri, RedirectAttributes rttr)
			throws Exception {

		log.info("상품 수정정보 : " + vo);

		// 상품이미지 변경(업로드) 유무
		if (!uploadFile.isEmpty()) {

			// 기존 상품이미지 삭제 - 날짜폴더명, 파일명
			FileManagerUtils.delete(uploadPath, vo.getPro_up_folder(), vo.getPro_img(), "image");

			// 변경이미지 업로드
			String dateFolder = FileManagerUtils.getDateFolder();
			String saveFileName = FileManagerUtils.uploadFile(uploadPath, dateFolder, uploadFile);

			// 새로운 파일명, 날짜 폴더명
			vo.setPro_img(saveFileName);
			vo.setPro_up_folder(dateFolder);

		}

		// 공통
		// db저장(update)
		adminProductService.productEdit(vo);

		return "redirect:/admin/product/productlist" + cri.getListLink();
	}

	// 상품삭제
	@PostMapping("/productdelete")
	public String productDelete(Criteria cri, Integer pro_num) throws Exception {

		adminProductService.productDelete(pro_num);

		return "redirect:/admin/product/productlist" + cri.getListLink();
	}

	// 체크상품 수정작업1
	@PostMapping("/productcheckedmodify1")
	public ResponseEntity<String> productCheckedModify1(
			@RequestParam("pro_num_arr") List<Integer> pro_num_arr,
			@RequestParam("pro_price_arr") List<Integer> pro_price_arr,
			@RequestParam("pro_buy_arr") List<String> pro_buy_arr) throws Exception {
		
		log.info("상품코드 : " + pro_num_arr);
		log.info("상품가격 : " + pro_price_arr);
		log.info("판매여부 : " + pro_buy_arr);
		
		ResponseEntity<String> entity = null;
		
		adminProductService.productCheckedModify1(pro_num_arr, pro_price_arr, pro_buy_arr);
		
		entity = new ResponseEntity<>("success", HttpStatus.OK);
			
		return entity;
	}
	
	// 체크상품 수정작업2
		@PostMapping("/productcheckedmodify2")
		public ResponseEntity<String> productCheckedModify2(
				@RequestParam("pro_num_arr") List<Integer> pro_num_arr,
				@RequestParam("pro_price_arr") List<Integer> pro_price_arr,
				@RequestParam("pro_buy_arr") List<String> pro_buy_arr) throws Exception {
			
			log.info("상품코드 : " + pro_num_arr);
			log.info("상품가격 : " + pro_price_arr);
			log.info("판매여부 : " + pro_buy_arr);
			
			ResponseEntity<String> entity = null;
			
			adminProductService.productCheckedModify2(pro_num_arr, pro_price_arr, pro_buy_arr);
			
			entity = new ResponseEntity<>("success", HttpStatus.OK);
				
			return entity;
		}

}
