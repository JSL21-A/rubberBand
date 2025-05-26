package com.TTT.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
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

import com.TTT.domain.BandRecruitPostVo;
import com.TTT.service.BandRecruitPostService;

@Controller
// 구인구직
@RequestMapping("/bandlist")
public class BandListController {

	@Autowired
	private BandRecruitPostService bandrecruitpostservice;

	// 리스트 (페이징 포함)
	@GetMapping("/list")
	public String BandList(
	    @RequestParam(value = "page", defaultValue = "1") int page,
	    Model model
	) {
	    int size = 6;
	    int totalPosts = bandrecruitpostservice.getTotalPostCount();
	    int totalPages = (int) Math.ceil((double) totalPosts / size);

	    List<BandRecruitPostVo> postList = bandrecruitpostservice.getRecruitPostsByPage(page, size);

	    model.addAttribute("postList", postList);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);

	    return "band/list";
	}

	
	// 구인구직 insert
	@PostMapping("/insert")
	public String insertRecruitPost(BandRecruitPostVo Vo, @RequestParam("band_id") Long band_id,
	    @RequestParam("title") String title, @RequestParam("bandIntro") String bandIntro,
	    @RequestParam("recruitPosition") String recruitPosition, @RequestParam("activityArea") String activityArea,
	    @RequestParam("recruitCondition") String recruitCondition, @RequestParam("preferredGenres") String preferredGenres,
	    @RequestParam("leaderComment") String leaderComment, @RequestParam("deadline") String deadline,
	    @RequestParam("tagKeywords") String tagKeywords, @RequestParam("selectedGenres") String selectedGenres,
	    @RequestParam("selectedPositions") String selectedPositions, @RequestParam("selectedGenders") String selectedGenders,
	    @RequestParam("selectedAges") String selectedAges, @RequestParam("bandImages") MultipartFile[] files,
	    Principal principal) throws IOException {

	    String username = principal.getName();
	    String userId = bandrecruitpostservice.findUserIdByUsername(username);
	    
	    BandRecruitPostVo vo = new BandRecruitPostVo();
	    vo.setBand_id(band_id);
	    vo.setUserId(userId);
	    vo.setTitle(title);
	    vo.setBandIntro(bandIntro);
	    vo.setRecruitPosition(recruitPosition);
	    vo.setActivityArea(activityArea);
	    vo.setRecruitCondition(recruitCondition);
	    vo.setPreferredGenres(preferredGenres);
	    vo.setLeaderComment(leaderComment);
	    vo.setTagKeywords(tagKeywords);
	    if (deadline != null && !deadline.isBlank()) {
	        vo.setDeadline(Date.valueOf(deadline));
	    }

	    // 등록하고 postId 반환받기
	    Long postId = bandrecruitpostservice.insertRecruitPostWithTags(vo, files, selectedGenres, selectedPositions, selectedGenders, selectedAges);

	    return "redirect:/bandlist/list?postId=" + postId;

	}

	@GetMapping("/mybands/all")
	@ResponseBody
	public List<BandRecruitPostVo> getMyBandList(Principal principal) {
	    String username = principal.getName();
	    String userId = bandrecruitpostservice.findUserIdByUsername(username);
	    return bandrecruitpostservice.getMyBandList(userId);
	}
	


}
