package com.TTT.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

			@RequestParam(value = "age", required = false, defaultValue = "") String age, Model model,
			Principal principal) {

		// 게시글 정보 불러오기
		BandRecruitPostVo post = bandRecruitPostSelectService.getPostById(postId);
		model.addAttribute("post", post);

		// postId로 bandId를 추출
		Long bandId = post.getBand_id(); // 게시글에서 bandId를 불러옴

		// 밴드 리더 정보 가져오기
		BandInsertVo leaderInfo = bandinsertselectservice.selectLeaderInfo(bandId);
		model.addAttribute("leader", leaderInfo);

		// 밴드 멤버 정보 가져오기（리더제외）
		List<BandInsertVo> members = bandinsertselectservice.getBandMembers(bandId);
		String leaderUserId = leaderInfo.getUser_id(); // UUID 기준
		members.removeIf(member -> leaderUserId.equals(member.getUser_id()));

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


		boolean isScrapped = false;
		if (principal != null) {
			String username = principal.getName();
			String userId = bandRecruitPostSelectService.findUserIdByUsername(username);
			if (userId != null) {
				isScrapped = bandRecruitPostSelectService.hasAlreadyScrapped(postId, userId);
			}
		}
		model.addAttribute("isScrapped", isScrapped);
		
		boolean isLeader = false;
		if (principal != null) {
			String username = principal.getName(); // 로그인한 사용자명
			String userId = bandinsertselectservice.findUserIdByUsername(username); //username 사용
			System.out.println("로그인한 사용자 ID: " + userId);
			isLeader = bandinsertselectservice.isUserLeader(bandId, userId); // userId(UUID)로 리더 여부 체크
		}
		model.addAttribute("isLeader", isLeader);


		return "band/view"; // 해당 게시글에 대한 상세보기 페이지
	}

	// 이력서
	@GetMapping("/resume/list")
	@ResponseBody
	public List<Long> getResumeIdList(Principal principal) {
		String username = principal.getName();
		System.out.println("✅ username: " + username);

		String userId = bandRecruitPostSelectService.findUserIdByUsername(username);
		System.out.println("✅ userId (UUID): " + userId);

		List<Long> resumes = bandRecruitPostSelectService.getAllResumeIdsByUserId(userId);
		System.out.println("✅ 불러온 이력서 개수: " + resumes.size());

		return resumes;
	}

	// 지원하기
	@PostMapping("/apply")

	public String applyToPost(@RequestParam("postId") Long postId, @RequestParam(value = "resume_id", required = false) Long resumeId,
			@RequestParam("band_id") Long bandId, RedirectAttributes redirectAttributes, Principal principal) {

		if (principal == null) {
			throw new RuntimeException("ログインされた使用者のみに接近出来ます。");
		}

		String username = principal.getName();
		String userId = bandRecruitPostSelectService.findUserIdByUsername(username); // UUID

		if (userId == null) {
			throw new RuntimeException("유효한 user_id를 찾을 수 없습니다.");
		}

		System.out.println("post_id: " + postId + ", resume_id: " + resumeId + ", band_id: " + bandId);

		  // ✅ 이력서 없을 경우 에러 메시지 리다이렉트
		if (resumeId == null) {
			return "redirect:/bandselect/view?postId=" + postId + "&band_id=" + bandId + "&message=noresume";
		}

		// 지원 중복 방지 (한 band_id 당 한 번만 지원가능)
		boolean alreadyApplied = bandRecruitPostSelectService.hasAlreadyApplied(postId, userId);
		if (!alreadyApplied) {
			bandRecruitPostSelectService.insertApplication(postId, userId, resumeId, bandId);
		}

		// FlashAttribute 없이 URL 파라미터로 메시지 전달
		String message = alreadyApplied ? "duplicate" : "success";

		return "redirect:/bandselect/view?postId=" + postId + "&band_id=" + bandId + "&resume_id=" + resumeId
				+ "&message=" + message;
	}

	// 스크랩
	@PostMapping("/scrap")
	public String insertScrap(@RequestParam("postId") Long postId, Principal principal) {
		if (principal == null) {
			throw new RuntimeException("ログインが必要とされます。");
		}

		String username = principal.getName();
		String userId = bandRecruitPostSelectService.findUserIdByUsername(username);

		if (userId == null) {
			throw new RuntimeException("ログインしてください。");
		}

		boolean alreadyScrapped = bandRecruitPostSelectService.hasAlreadyScrapped(postId, userId);

		if (alreadyScrapped) {
			bandRecruitPostSelectService.deleteScrap(postId, userId); // 해제
		} else {
			bandRecruitPostSelectService.insertScrap(postId, userId); // 추가
		}

		int status = alreadyScrapped ? 0 : 1;
		return "redirect:/bandselect/view?postId=" + postId + "&scrap=" + status;
	}
	
	// 구인구직 글 삭제
	@PostMapping("/delete")
	public String deletePost(@RequestParam("postId") Long postId, Principal principal) {
	    if (principal == null) {
	        throw new RuntimeException("ログインが必要です。");
	    }

	    String username = principal.getName();
	    String userId = bandRecruitPostSelectService.findUserIdByUsername(username);
	    BandRecruitPostVo post = bandRecruitPostSelectService.getPostById(postId);

	    // 리더가 맞는지 체크
	    boolean isLeader = bandinsertselectservice.isUserLeader(post.getBand_id(), userId);
	    if (!isLeader) {
	        throw new RuntimeException("削除権限がありません。");
	    }

	    bandRecruitPostSelectService.deletePostById(postId);
	    return "redirect:/bandlist/list"; 
	}


	
}
