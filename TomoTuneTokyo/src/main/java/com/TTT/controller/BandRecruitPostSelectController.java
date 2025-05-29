package com.TTT.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TTT.domain.BandInsertVo;
import com.TTT.domain.BandRecruitPostVo;
import com.TTT.service.BandInsertSelectService;
import com.TTT.service.BandRecruitPostSelectService;

@Controller
@RequestMapping("/bandselect")
public class BandRecruitPostSelectController {

	@Autowired
	private BandRecruitPostSelectService bandRecruitPostSelectService;

	@Autowired
	private BandInsertSelectService bandinsertselectservice;

	@GetMapping("/view")
	public String viewPost(@RequestParam("postId") Long postId,
            @RequestParam(value = "genre", required = false, defaultValue = "") String genre,
            @RequestParam(value = "position", required = false, defaultValue = "") String position,
            @RequestParam(value = "gender", required = false, defaultValue = "") String gender,
            @RequestParam(value = "age", required = false, defaultValue = "") String age, Model model) {

		System.out.println("genre: " + genre);
		System.out.println("position: " + position);
		System.out.println("gender: " + gender);
		System.out.println("age: " + age);

		// 게시글 정보 불러오기
		BandRecruitPostVo post = bandRecruitPostSelectService.getPostById(postId);
		model.addAttribute("post", post);

		// postId로 bandId를 추출
		Long bandId = post.getBand_id(); // 게시글에서 bandId를 가져옵니다

		// 밴드 리더 정보 가져오기
		BandInsertVo leaderInfo = bandinsertselectservice.selectLeaderInfo(bandId);
		model.addAttribute("leader", leaderInfo);

		// 밴드 멤버 정보 가져오기
		List<BandInsertVo> members = bandinsertselectservice.getBandMembers(bandId);
		model.addAttribute("members", members);

		// band_name
		String bandName = bandRecruitPostSelectService.getBandName(post.getBand_id()); 
		model.addAttribute("band_name", bandName);

		 // 조회수 증가
	    bandRecruitPostSelectService.incrementViewCount(postId);

	    // 최신 조회수 값 가져오기
	    Long bandrecruitpost_count = bandRecruitPostSelectService.getViewCount(postId);

	    // 조회수 값을 post 객체에 추가
	    post.setBandrecruitpost_count(bandrecruitpost_count);
	    
	    List<String> recommendedTags = bandRecruitPostSelectService.getRecommendedTags(postId);
	    model.addAttribute("recommendedTags", recommendedTags);

		    
		return "band/view"; // 해당 게시글에 대한 상세보기 페이지
	}
}
