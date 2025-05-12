package com.TTT.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.TTT.domain.BandInsertVo;
import com.TTT.service.BandInsertService;


@Controller
@RequestMapping("/bandinsert")
public class MemberInsertController {
	
	@Autowired
	private BandInsertService bandInsertService;
	
	// 밴드 상세정보
	@GetMapping("/modify")
	public String modify() {
		return "/bandlist/modify";
	}
	
	// 밴드 리스트 확인 가능
	@GetMapping("/modifylist")
	public String modifylist(Model model) {
	    List<BandInsertVo> bandList = bandInsertService.getAllBands();
	    model.addAttribute("bandList", bandList);
	    return "bandlist/modifylist";

	}
	
	// 밴드 결성 입력폼 (저장 기능 포함)
		@PostMapping("/bandsinsert")
		public String insert(
		    BandInsertVo vo,
		    @RequestParam("bandProfileImage") MultipartFile bandProfileImage,
		    @RequestParam("bandCoverImage") MultipartFile bandCoverImage
		) {
		    bandInsertService.bandInsert(vo, bandProfileImage, bandCoverImage);
		    return "redirect:/bandinsert/modifylist";
		}
		
	// 밴드 결성 입력폼 멤버 검색기능
		@GetMapping("/membersearch")
		@ResponseBody
		public List<BandInsertVo> searchMembers(@RequestParam("keyword") String keyword) {
		    return bandInsertService.searchMembersByStageName(keyword);
		}
		
//		// 리더용 (단일 유저 정보)
//		@GetMapping("/membersearch/leader")
//		@ResponseBody
//		public BandInsertVo getLeader(@SessionAttribute("loginUser") String userId) {
//		    return bandInsertService.getLeaderInfo(userId);
//		}
//
//		// 일반 멤버용
//		@GetMapping("/membersearch/general")
//		@ResponseBody
//		public List<BandInsertVo> searchMembers(
//		        @RequestParam("keyword") String keyword,
//		        @SessionAttribute("loginUser") String userId
//		) {
//		    return bandInsertService.searchMembersByStageName(keyword, userId);
//		}


	
	
}
