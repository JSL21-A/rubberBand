package com.TTT.controller;

import java.nio.file.Path;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TTT.domain.MyActiveDto;
import com.TTT.domain.MypageDto;
import com.TTT.domain.PostVo;
import com.TTT.domain.UserDto;
import com.TTT.domain.UserProfileDto;
import com.TTT.security.CustomUserDetails;
import com.TTT.service.MypageService;
import com.TTT.service.UserService;

import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.security.Principal;

@Controller
@RequestMapping("/mypage")
public class MypageController {

	private final MypageService mypageService;

	@Autowired
	private UserService userService;

	public MypageController(MypageService mypageService) {
		this.mypageService = mypageService;
	}

	// 유저 아이디 추출 공통 함수
	private String getCurrentUserId(Principal principal) {
		String username = principal.getName();
		return userService.findByUsername(username).getUser_id();
	}

	// 이력서 입력 폼
	@GetMapping("/resumeInsert")
	public String resumeInsertForm() {
		return "mypage/resumeInsert";
	}

	// 이력서 저장
	@PostMapping("/insert")
	public String insertResume(@ModelAttribute MypageDto dto,
			@RequestParam(required = false, name = "instrument") List<String> instruments,
			@RequestParam(required = false, name = "instrumentEtc") String instrumentEtc, Principal principal,
			RedirectAttributes redirectAttributes) {

		String userId = getCurrentUserId(principal);

		if (mypageService.hasResume(userId)) {
			redirectAttributes.addFlashAttribute("errorMessage", "すでに履歴書があります。");
			return "redirect:/mypage/account";
		}

		if (instruments != null && !instruments.isEmpty()) {
			instruments.removeIf(i -> i.equalsIgnoreCase("etc"));
			if (instrumentEtc != null && !instrumentEtc.isBlank()) {
				instruments.add(instrumentEtc.trim());
			}
			dto.setInstrument(String.join(",", instruments));
		}

		dto.setUserId(userId);
		mypageService.saveResume(dto);
		return "redirect:/mypage/resumeView";
	}

	// 이력서 조회
	@GetMapping("/resumeView")
	public String viewResume(Model model, Principal principal) {
		String userId = getCurrentUserId(principal);
		MypageDto resume = mypageService.getResumeByUserId(userId);

		model.addAttribute("resume", resume);
		model.addAttribute("areaList", toList(resume.getArea()));
		model.addAttribute("instrumentList", toList(resume.getInstrument()));
		model.addAttribute("genreList", toList(resume.getGenre()));
		model.addAttribute("practiceDayList", toList(resume.getPracticeDate()));

		return "mypage/resumeView";
	}

	// 공통 분리 함수
	private List<String> toList(String input) {
		if (input == null || input.isBlank()) {
			return Collections.emptyList();
		}
		return Arrays.stream(input.split(",")).map(String::trim).toList();
	}

	// 일본어 요일로 변환 함수 (DB 저장 형식 -> 뷰용)
	private List<String> toJapaneseDays(String input) {
		if (input == null || input.isBlank())
			return Collections.emptyList();

		return Arrays.stream(input.split(",")).map(day -> switch (day) {
		case "MON" -> "月";
		case "TUE" -> "火";
		case "WED" -> "水";
		case "THU" -> "木";
		case "FRI" -> "金";
		case "SAT" -> "土";
		case "SUN" -> "日";
		default -> day;
		}).toList();
	}

	// 설정페이지
	@GetMapping("/account")
	public String accountSetting(Model model, Principal principal) {
		String userId = getCurrentUserId(principal);
		boolean hasResume = mypageService.hasResume(userId);
		// 유저 프로필 불러오기
		UserProfileDto userProfile = mypageService.getUserProfileByUserId(userId);
		model.addAttribute("userProfile", userProfile);
		model.addAttribute("hasResume", hasResume);
		return "mypage/accountSetting_popup";
	}

	// 이력서 삭제
	@PostMapping("/delete")
	@ResponseBody
	public ResponseEntity<?> deleteResume(Principal principal) {
		String userId = getCurrentUserId(principal);
		boolean deleted = mypageService.deleteResumeByUserId(userId);

		if (deleted) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("削除失敗");
		}
	}

	// 이력서 수정 폼
	@GetMapping("/resumeEdit/{id}")
	public String showEditForm(@PathVariable("id") Long id, Model model, Principal principal) {
		String userId = getCurrentUserId(principal);

		MypageDto resume = mypageService.getResumeById(id);

		if (resume == null || !resume.getUserId().equals(userId)) {
			return "redirect:/mypage/account";
		}

		List<String> instruments = toList(resume.getInstrument());
		List<String> genres = toList(resume.getGenre());

		List<String> knownInstruments = List.of("Vo", "Gt", "Ba", "Dr", "Kb");
		String instrumentEtc = instruments.stream().filter(i -> !knownInstruments.contains(i)).findFirst().orElse(null);

		List<String> knownGenres = List.of("ロック", "ポップ", "ジャズ", "ヒップホップ", "クラシック", "EDM");
		String genreEtc = genres.stream().filter(g -> !knownGenres.contains(g)).findFirst().orElse(null);

		model.addAttribute("resume", resume);
		model.addAttribute("areaList", toList(resume.getArea()));
		model.addAttribute("instrumentList", instruments);
		model.addAttribute("instrumentEtc", instrumentEtc);
		model.addAttribute("genreList", genres);
		model.addAttribute("genreEtc", genreEtc);

		// 연습요일 영어 → 일본어로 변환 후 전달
		model.addAttribute("selectedDays", toJapaneseDays(resume.getPracticeDate()));

		// 연습시간대 대문자 → 소문자 변환 후 전달
		String practiceTime = resume.getPracticeTime();
		if (practiceTime != null) {
			practiceTime = practiceTime.toLowerCase();
		}
		model.addAttribute("practiceTime", practiceTime);

		model.addAttribute("detailTime", resume.getDetailTime());
		model.addAttribute("bandHistoryList", resume.getBandHistoryList());
		model.addAttribute("note", resume.getNote());

		return "mypage/resumeEdit";
	}

	// 이력서 수정 저장
	@PostMapping("/update")
	public String updateResume(@ModelAttribute MypageDto dto,
			@RequestParam(required = false, name = "instrument") List<String> instruments,
			@RequestParam(required = false, name = "instrumentEtc") String instrumentEtc,
			@RequestParam(required = false, name = "genreList") List<String> genres,
			@RequestParam(required = false, name = "genreEtc") String genreEtc, Principal principal,
			RedirectAttributes redirectAttributes) {

		String userId = getCurrentUserId(principal);

		if (!dto.getUserId().equals(userId)) {
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			return "redirect:/mypage/account";
		}

		if (instruments != null) {
			instruments.removeIf(i -> i != null && i.equalsIgnoreCase("etc"));
			if (instrumentEtc != null && !instrumentEtc.isBlank()) {
				instruments.add(instrumentEtc.trim());
			}
			dto.setInstrument(String.join(",", instruments));
		}

		if (genres != null) {
			if (genreEtc != null && !genreEtc.isBlank()) {
				genres.add(genreEtc.trim());
			}
			dto.setGenre(String.join(",", genres));
		}

		mypageService.updateResume(dto);
		return "redirect:/mypage/resumeView";
	}

	// 프로필 편집 페이지로 이동 (GET)
	@GetMapping("/profileEdit")
	public String profileEdit(Model model, Principal principal) {
		String userId = getCurrentUserId(principal);
		UserProfileDto userProfile = mypageService.getUserProfileByUserId(userId);
		if (userProfile == null) {
			return "redirect:/mypage/account";
		}
		model.addAttribute("userProfile", userProfile);
		return "mypage/profileEdit"; // 프로필 편집 뷰
	}

	@PostMapping("/profileEdit")
	public String profileEditSubmit(@ModelAttribute UserProfileDto userProfileDto,
			@RequestParam("profilePhoto") MultipartFile profilePhoto, Principal principal,
			RedirectAttributes redirectAttrs) throws IOException {

		String userId = getCurrentUserId(principal);
		UserProfileDto existingProfile = mypageService.getUserProfileByUserId(userId);

		// 닉네임 null/빈문자열일 때 기존 닉네임 사용
		String nickname = userProfileDto.getNickname();
		if (nickname == null || nickname.trim().isEmpty()) {
			nickname = existingProfile.getNickname();
		}
		nickname = nickname.trim();

		// 닉네임 유효성 검사
		if (!nickname.matches("^[ぁ-んァ-ン一-龥a-zA-Z0-9가-힣]+$")) {
			redirectAttrs.addFlashAttribute("errorMessage", "ニックネームに空白や記号は使えません。");
			return "redirect:/mypage/profileEdit";
		}
		if (nickname.matches("^[0-9]+$")) {
			redirectAttrs.addFlashAttribute("errorMessage", "数字だけのニックネームは使えません。別のニックネームを入力してください。");
			return "redirect:/mypage/profileEdit";
		}
		if (nickname.length() > 10) {
			redirectAttrs.addFlashAttribute("errorMessage", "ニックネームは10文字以内で入力してください。");
			return "redirect:/mypage/profileEdit";
		}
		if (!nickname.equals(existingProfile.getNickname()) && mypageService.isNicknameDuplicate(nickname)) {
			redirectAttrs.addFlashAttribute("errorMessage", "このニックネームは既に使われています。別のニックネームを入力してください。");
			return "redirect:/mypage/profileEdit";
		}

		if (!userId.equals(userProfileDto.getUserId())) {
			return "redirect:/mypage/account";
		}

		// 최종 닉네임 세팅
		userProfileDto.setNickname(nickname);

		// 프로필 사진 업로드 처리
		if (profilePhoto != null && !profilePhoto.isEmpty()) {
			String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/uploads/";
			System.out.println("Upload directory: " + uploadDir);

			String originalFilename = profilePhoto.getOriginalFilename();
			String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
			String newFilename = userId + "_" + System.currentTimeMillis() + ext;

			// 디렉토리 만들기
			Path uploadPath = Paths.get(uploadDir);
			Files.createDirectories(uploadPath);

			Path fullPath = uploadPath.resolve(newFilename);
			profilePhoto.transferTo(fullPath.toFile());

			System.out.println("파일 저장 여부 확인: " + fullPath.toFile().exists());

			// DB에는 웹에서 접근할 수 있는 상대 경로만 저장
			userProfileDto.setUserImg("images/uploads/" + newFilename);

			// 기존 파일 삭제 (새 파일 저장된 경우에만)
			if (existingProfile.getUserImg() != null && !existingProfile.getUserImg().isEmpty()) {
				Path oldImagePath = Paths.get(System.getProperty("user.dir"), existingProfile.getUserImg());
				Files.deleteIfExists(oldImagePath);
			}

			// 새 이미지로 저장
			userProfileDto.setUserImg("/uploads/" + newFilename);

		} else {
			// 사진 미업로드 시 기존 이미지 유지
			userProfileDto.setUserImg(existingProfile.getUserImg());
		}

		// 서비스 업데이트 호출
		mypageService.updateUserProfile(userProfileDto);
		redirectAttrs.addFlashAttribute("successMessage", "プロフィールが更新されました。");
		
		String username = principal.getName();
		UserDto updatedUser = userService.findByUsername(username);
		CustomUserDetails newDetails = new CustomUserDetails(updatedUser);
		Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> roles = currentAuth.getAuthorities();
		
		UsernamePasswordAuthenticationToken newAuth = 
				new UsernamePasswordAuthenticationToken(newDetails, currentAuth.getCredentials(), roles);
		
		SecurityContextHolder.getContext().setAuthentication(newAuth);

		return "redirect:/mypage/account";
	}

	// 비밀번호 변경 (변경버튼 클릭 > 현재 비밀번호 인증 + 새로운 비밀번호 입력 + 일치여부확인)
	// 비밀번호 인증
	@PostMapping("/update-password")
	@ResponseBody
	public Map<String, Object> updatePassword(@RequestBody Map<String, String> body, Principal principal) {
		Map<String, Object> result = new HashMap<String, Object>();
		String userId = getCurrentUserId(principal);

		String currentPassword = body.get("currentPassword");
		String newPassword = body.get("newPassword");

		// 1. 현재 비밀번호 확인
		boolean match = mypageService.checkCurrentPassword(userId, currentPassword);
		if (!match) {
			result.put("success", false);
			result.put("message", "現在のパスワードが正しくありません。");
			return result;
		}

		// 2. 비밀번호 업데이트
		boolean updated = mypageService.updateUserPassword(userId, newPassword);
		result.put("success", updated);
		return result;
	}

	// 나의활동 이동 (투고, 코멘트, 스크랩, 지원현황)

	// 나의활동 페이지
	@GetMapping("/activity")
	public String redirectToDefaultActivity() {
		return "redirect:/mypage/activity/posts";
	}

	// 나의활동 네비바
	@GetMapping("/activity/{mode}")
	public String viewMyActivity(@PathVariable("mode") String mode, Principal principal, Model model) {
		String userId = getCurrentUserId(principal);

		if ("posts".equals(mode)) {
			List<PostVo> postContentList = mypageService.getMyPosts(userId);
			model.addAttribute("postContentList", postContentList);
		} else if ("comments".equals(mode)) {
			List<MyActiveDto> commentContentList = mypageService.getMyComments(userId);
			model.addAttribute("commentContentList", commentContentList);
		} else {
			// 스크랩, 지원현황 추가 예정
		}

		model.addAttribute("mode", mode);
		return "mypage/myActive";
	}

	// 게시글 이동
	@GetMapping("/user/view")
	public String viewPostByQuery(@RequestParam("post") Long postId, Model model) {
		PostVo post = mypageService.getPostById(postId);

		if (post == null) {
			return "error/404";
		}

		model.addAttribute("post", post);
		return "board/postDetail";
	}

	// 댓글 삭제
	@PostMapping("/deleteComments")
	public String deleteComments(@RequestParam("commentIds") List<Long> commentIds, Principal principal,
			RedirectAttributes redirectAttributes) {
		String userId = getCurrentUserId(principal);

		// 서비스에서 삭제 처리
		boolean success = mypageService.deleteCommentsByIds(userId, commentIds);

		if (success) {
			redirectAttributes.addFlashAttribute("successMessage", "コメントが削除されました。");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "削除に失敗しました。");
		}

		return "redirect:/mypage/activity/comments";
	}

	// 게시글 삭제
	@PostMapping("/deletePosts")
	public String deletePosts(@RequestParam("postIds") List<Long> postIds, Principal principal,
			RedirectAttributes redirectAttributes) {
		String userId = getCurrentUserId(principal);
		boolean deleted = mypageService.deletePostsByIds(userId, postIds);

		if (deleted) {
			redirectAttributes.addFlashAttribute("successMessage", "投稿が削除されました。");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "削除に失敗しました。");
		}

		return "redirect:/mypage/activity/posts";
	}
	
	//지원현황(리더)
	@GetMapping("/activity/applies")
	public String viewApplyStatus(Model model, Principal principal) {
	    String userId = getCurrentUserId(principal);
	    List<MyActiveDto> applyStatusList = mypageService.getApplyStatusByWriter(userId);
	    model.addAttribute("applyStatusList", applyStatusList);
	    model.addAttribute("mode", "applies");
	    return "mypage/myActive";
	}


}
