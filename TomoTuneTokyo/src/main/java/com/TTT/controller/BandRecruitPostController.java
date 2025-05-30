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

import com.TTT.domain.BandInsertVo;
import com.TTT.domain.BandRecruitPostVo;
import com.TTT.service.BandRecruitPostService;

@Controller
// 구인구직
@RequestMapping("/bandlist")
public class BandRecruitPostController {

	@Autowired
	private BandRecruitPostService bandrecruitpostservice;

	// 리스트 (페이징 포함)
	@GetMapping("/list")
	public String BandList(@RequestParam(value = "postId", required = false) Long postId,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "keyword", required = false) String keyword, Model model) {
		int size = 6;

		List<BandRecruitPostVo> postList;
		int totalPages;
		int currentPage;

		if (keyword != null && !keyword.isBlank()) {
			// 검색어가 있을 때
			postList = bandrecruitpostservice.searchByTeamNameOrPosition(keyword);

			// band_name
			for (BandRecruitPostVo post : postList) {
				String bandName = bandrecruitpostservice.getBandNameById(post.getBand_id());
				post.setBand_name(bandName);
			}

			totalPages = 1; // 검색 결과는 페이징 없이 1페이지로 처리
			currentPage = 1;
			model.addAttribute("keyword", keyword);
		} else {
			// 검색어 없을 때 - 전체 목록 페이징
			int totalPosts = bandrecruitpostservice.getTotalPostCount();
			totalPages = (int) Math.ceil((double) totalPosts / size);
			postList = bandrecruitpostservice.getRecruitPostsByPage(page, size);

			for (BandRecruitPostVo post : postList) {
				String bandName = bandrecruitpostservice.getBandNameById(post.getBand_id());
				post.setBand_name(bandName);
			}

			currentPage = page;
		}

		model.addAttribute("postList", postList);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);

		return "band/list";
	}

	// 구인구직 insert
	@PostMapping("/insert")
	public String insertRecruitPost(BandRecruitPostVo Vo, @RequestParam("band_id") Long band_id,
			@RequestParam("title") String title, @RequestParam("bandIntro") String bandIntro,
			@RequestParam("recruitPosition") String recruitPosition, @RequestParam("activityArea") String activityArea,
			@RequestParam("recruitCondition") String recruitCondition,
			@RequestParam("preferredGenres") String preferredGenres,
			@RequestParam("leaderComment") String leaderComment, @RequestParam("deadline") String deadline,
			@RequestParam("tagKeywords") String tagKeywords, @RequestParam("selectedGenres") String selectedGenres,
			@RequestParam("selectedPositions") String selectedPositions,
			@RequestParam("selectedGenders") String selectedGenders, @RequestParam("selectedAges") String selectedAges,
			@RequestParam("bandImages") MultipartFile[] files, Principal principal) throws IOException {

		String username = principal.getName();
		String userId = bandrecruitpostservice.findUserIdByUsername(username);

		BandRecruitPostVo vo = new BandRecruitPostVo();
		vo.setBand_id(band_id);
		vo.setUserId(userId);
		vo.setTitle(title);
		vo.setBand_intro(bandIntro);
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
		Long postId = bandrecruitpostservice.insertRecruitPostWithTags(vo, files, selectedGenres, selectedPositions,
				selectedGenders, selectedAges);

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
