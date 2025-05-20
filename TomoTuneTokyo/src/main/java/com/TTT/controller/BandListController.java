package com.TTT.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



@Controller
// 구인구직
@RequestMapping("/bandlist")
public class BandListController {

//	@Autowired
//	private BandListMapper bandListMapper;
//	
//	private final String uploadDir = "C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"; // 이미지 저장 경로
//	
//	// 구인구직 리스트
//	@GetMapping("/list")
//		public String BandList() {
//
//		return "band/list";
//	}
//
//	
//	// 구인구직 입력폼
//	@GetMapping("/listinsert")
//	public String BandMemeberInsert(BandInsertSelectVo vo,  @RequestParam("band_images") MultipartFile[] band_images) {
//	// 1. UUID 생성
//	String postId = UUID.randomUUID().toString();
//	vo.setPost_id(postId);
//
//	// 2. 고정값 세팅
//	vo.setBoard_id("RECRUIT");
//	vo.setPost_status("A");
//	vo.setPost_like(0);
//
//	// 3. 대표 이미지 저장 (band_images[0])
//	if (band_images != null && band_images.length > 0 && !band_images[0].isEmpty()) {
//		try {
//			String fileName = UUID.randomUUID() + "_" + band_images[0].getOriginalFilename();
//			Path filePath = Paths.get(uploadDir + fileName);
//			Files.copy(band_images[0].getInputStream(), filePath);
//			vo.setPost_img(fileName);
//		} catch (IOException e) {
//			throw new RuntimeException("대표 이미지 저장 실패", e);
//		}
//	} else {
//		vo.setPost_img(null);
//	}
//
//	// 4. post_content 구성 (소개, 조건 등 조합 가능)
//	String content = vo.getPost_title() + "\n\n" +
//	                 "🎸 募集ポジション: " + vo.getRecruit_position() + "\n" +
//	                 "📍 活動地域: " + vo.getActivity_area() + "\n" +
//	                 "🧠 好きなジャンル: " + vo.getPreferred_genres() + "\n" +
//	                 "💬 リーダーコメント: " + vo.getLeader_comment() + "\n" +
//	                 "📅 締切: " + vo.getDeadline();
//	vo.setPost_content(content);
//
//	// 5. insert 실행
//	bandListMapper.BandMemeberInsert(vo);
//	return "redirect:/bandlist/list";
//	}
//	
//	@GetMapping("/view")
//	public String viewPortfolio() {
//		return "/band/view";
//	}
//	
}

