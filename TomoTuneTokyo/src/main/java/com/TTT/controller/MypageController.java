package com.TTT.controller;

import java.nio.file.Path;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TTT.domain.MypageDto;
import com.TTT.domain.UserDto;
import com.TTT.domain.UserProfileDto;
import com.TTT.service.MypageService;
import com.TTT.service.UserService;

import java.nio.file.Paths; 
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


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
			@RequestParam(required = false, name = "instrumentEtc") String instrumentEtc,
			Principal principal,
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
		if (input == null || input.isBlank()) return Collections.emptyList();

		return Arrays.stream(input.split(","))
			.map(day -> switch(day) {
				case "MON" -> "月";
				case "TUE" -> "火";
				case "WED" -> "水";
				case "THU" -> "木";
				case "FRI" -> "金";
				case "SAT" -> "土";
				case "SUN" -> "日";
				default -> day;
			})
			.toList();
	}

	// 설정페이지
	@GetMapping("/account")
	public String accountSetting(Model model, Principal principal) {
	    String userId = getCurrentUserId(principal);
	    boolean hasResume = mypageService.hasResume(userId);
	    //유저 프로필 불러오기
	    UserProfileDto userProfile = mypageService.getUserProfileByUserId(userId);
	    model.addAttribute("userProfile", userProfile);
	    model.addAttribute("hasResume", hasResume);
	    return "mypage/accountSetting";
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
	public String updateResume(
			@ModelAttribute MypageDto dto,
			@RequestParam(required = false, name = "instrument") List<String> instruments,
			@RequestParam(required = false, name = "instrumentEtc") String instrumentEtc,
			@RequestParam(required = false, name = "genreList") List<String> genres,
			@RequestParam(required = false, name = "genreEtc") String genreEtc,
			Principal principal,
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
	    return "mypage/profileEdit";  // 프로필 편집 뷰
	}

	@PostMapping("/profileEdit")
	public String profileEditSubmit(
	        @ModelAttribute UserProfileDto userProfileDto,
	        @RequestParam("profilePhoto") MultipartFile profilePhoto,
	        Principal principal,
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
	        redirectAttrs.addFlashAttribute("errorMessage", "ニックネームに空白や記号は使えません。別のニックネームを入力してください。");
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
	        String uploadDir = "src/main/resources/static/images/uploads/";
	        String originalFilename = profilePhoto.getOriginalFilename();
	        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
	        String newFilename = userId + "_" + System.currentTimeMillis() + ext;

	        Path path = Paths.get(uploadDir + newFilename);
	        Files.createDirectories(path.getParent());
	        profilePhoto.transferTo(path.toFile());

	        userProfileDto.setUserImg("images/uploads/" + newFilename);
	    } else {
	        // 사진 미업로드시 기존 이미지 유지
	        userProfileDto.setUserImg(existingProfile.getUserImg());
	    }

	    // 서비스 업데이트 호출
	    mypageService.updateUserProfile(userProfileDto);
	    redirectAttrs.addFlashAttribute("successMessage", "プロフィールが更新されました。");

	    return "redirect:/mypage/account";
	}



	
	
	
}
