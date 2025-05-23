package com.TTT.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.TTT.domain.BandActiveInsertVo;
import com.TTT.domain.BandInsertVo;
import com.TTT.domain.MemberType;
import com.TTT.service.BandInsertSelectService;


//밴드 결성 후 밴드 상세 조회
@Controller
@RequestMapping("/bandinsertselect")
public class BandInsertSelectController {

	@Autowired
	private BandInsertSelectService bandinsertselectservice;

	@GetMapping("/modify")
	public String showBandDetail(@RequestParam("band_id") Long bandId, Model model, Principal principal) {

		// 1. 밴드 기본 정보
		BandInsertVo band = bandinsertselectservice.getBandDetail(bandId);
		model.addAttribute("band", band);

		// 수정모드를 리더에게만 보이도록
		boolean isLeader = false;
		if (principal != null) {
			String username = principal.getName(); // 로그인한 사용자명
			String userId = bandinsertselectservice.findUserIdByUsername(username); // ✅ username 사용
			System.out.println("로그인한 사용자 ID: " + userId);
			isLeader = bandinsertselectservice.isUserLeader(bandId, userId); // ✅ userId(UUID)로 리더 여부 체크
		}
		model.addAttribute("isLeader", isLeader);

		// 2. 멤버 정보
		List<BandInsertVo> members = bandinsertselectservice.getBandMembers(bandId);
		model.addAttribute("members", members);

		// 3. 태그 정보
		List<BandInsertVo> tags = bandinsertselectservice.getBandTags(bandId);
		model.addAttribute("tags", tags);

		// 리더 이메일
		String leaderEmail = bandinsertselectservice.getLeaderEmail(bandId);
		model.addAttribute("leaderEmail", leaderEmail);

		// 리더 정보 조회
		BandInsertVo leaderInfo = bandinsertselectservice.selectLeaderInfo(bandId);
		model.addAttribute("leader", leaderInfo);

		// 활동 사진 리스트
		List<BandActiveInsertVo> activePhotos = bandinsertselectservice.getBandActivePhotos(bandId);
		System.out.println("조회된 활동 사진 수: " + (activePhotos != null ? activePhotos.size() : "null"));

		if (activePhotos == null) {
			activePhotos = new ArrayList<>(); // null 방지
		}
		model.addAttribute("galleryList", activePhotos);

		// 활동 기록 리스트 전체 조회 및 페이징 처리
		List<BandActiveInsertVo> recordList = bandinsertselectservice.getBandActivityRecords(bandId);
		System.out.println("조회된 활동 기록 수: " + (recordList != null ? recordList.size() : "null"));

		DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("M월");
		DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d");

		for (BandActiveInsertVo record : recordList) {
			if (record.getRecordDate() != null) {
				record.setFormattedDate(record.getRecordDate().format(monthFormatter)); // "5월"
				record.setFormattedDay(record.getRecordDate().format(dayFormatter)); // "22"

				if (record.getRecordDate().isAfter(LocalDate.now())) {
					record.setRecordStatus("예정");
				} else {
					record.setRecordStatus("완료");
				}
			}
		}

		model.addAttribute("recordList", recordList);

		// recordList를 4개씩 묶어 recordSlides로 전달 (2x2 레이아웃용)
		List<List<BandActiveInsertVo>> recordSlides = new ArrayList<>();
		if (recordList != null && !recordList.isEmpty()) {
			int groupSize = 4;
			for (int i = 0; i < recordList.size(); i += groupSize) {
				int end = Math.min(i + groupSize, recordList.size());
				recordSlides.add(recordList.subList(i, end));
			}
		}
		model.addAttribute("recordSlides", recordSlides);

		// null 방지
		if (recordList == null) {
			recordList = new ArrayList<>();
		}

		// 기본 recordList 전달 (뷰에서 전체 활동 기록 반복에 사용)
		model.addAttribute("recordList", recordList);

		// 4개씩 묶어서 페이징 가능한 구조로 변환
		List<List<BandActiveInsertVo>> recordChunks = new ArrayList<>();
		if (!recordList.isEmpty()) {
			int chunkSize = 4; // 페이지당 4개
			for (int i = 0; i < recordList.size(); i += chunkSize) {
				int end = Math.min(i + chunkSize, recordList.size());
				recordChunks.add(recordList.subList(i, end));
			}
		}
		model.addAttribute("recordChunks", recordChunks);

		return "/band/modify";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields("photo"); // vo.photo는 수동으로 처리
	}

	// 활동 사진 등록/수정용 (리더만 접근)
	@PostMapping("/activeinsert")
	public String insertPhoto(@RequestParam("bandId") Long bandId, @RequestParam("photo") MultipartFile photo,
			@RequestParam("title") String title, @RequestParam("description") String desc,
			@RequestParam(value = "youtubeUrl", required = false) String youtubeUrl, BandActiveInsertVo vo,
			Principal principal, Model model) {

		// 현재 로그인 사용자 ID → 리더 권한 판단에 사용 가능
		if (principal != null) {
			String username = principal.getName(); // 로그인된 사용자명 (email)

			// 리더 정보 조회 → 실제 UUID 형식 user_id 포함
			BandInsertVo leaderInfo = bandinsertselectservice.selectLeaderInfo(bandId);
			String userId = leaderInfo.getUser_id(); // UUID

			model.addAttribute("LeaderId", userId); // UUID를 model에 저장 (필요 시 화면 사용)

			// 활동 사진 등록 서비스 호출 (UUID 기반 userId 전달)
			bandinsertselectservice.insertBandGallery(photo, title, desc, youtubeUrl, bandId, userId);
		}

		// 활동 사진 정보 갱신 (null 방지 처리)
		List<BandActiveInsertVo> activePhotos = bandinsertselectservice.getBandActivePhotos(Long.valueOf(bandId));
		if (activePhotos == null) {
			activePhotos = new ArrayList<>(); // null 대신 빈 리스트
		}
		model.addAttribute("activePhotos", activePhotos);
		model.addAttribute("galleryList", activePhotos);

		// ✅ 다시 상세 페이지로 포워딩
		return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	// 활동 사진 수정
	@PostMapping("/activeupdate")
	public String updatePhoto(@RequestParam("activityPhotoId") Long activityPhotoId,
			@RequestParam("bandId") Long bandId,
			@RequestParam(value = "youtubeUrl", required = false) String youtubeUrl,
			@RequestParam("title") String title, @RequestParam("description") String desc,
			@RequestParam(value = "photo", required = false) MultipartFile photo, Principal principal) {

		BandActiveInsertVo vo = new BandActiveInsertVo();
		vo.setActivityPhotoId(activityPhotoId);
		vo.setActivityYoutubeUrl(youtubeUrl);
		vo.setActivityTitle(title);
		vo.setActivityContent(desc);

		// 이미지가 있는 경우 → 저장 경로로 복사 후 URL 설정
		if (photo != null && !photo.isEmpty()) {
			try {
				String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
				Path path = Paths.get(
						"C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"
								+ fileName);
				Files.copy(photo.getInputStream(), path);
				vo.setImageUrl(fileName);
			} catch (IOException e) {
				throw new RuntimeException("이미지 저장 실패", e);
			}
		}

		// DB 업데이트
		bandinsertselectservice.updateBandActivePhoto(vo);

		// 수정 후 해당 밴드 상세 페이지로 리디렉션
		return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	// 활동사진 조회 (AJAX 요청용)
	@GetMapping("/activeinfo")
	@ResponseBody
	public BandActiveInsertVo getPhotoInfo(@RequestParam("activityPhotoId") Long activityPhotoId) {
		return bandinsertselectservice.getActivityPhotoById(activityPhotoId);
	}

	// 활동 기록 등록 (리더만 접근)
	@PostMapping("/recordinsert")
	public String insertRecord(@RequestParam("bandId") Long bandId,
			@RequestParam("recordImage") MultipartFile recordImage, @RequestParam("recordTitle") String title,
			@RequestParam("recordDate") String date, @RequestParam("recordTime") String time,
			@RequestParam("recordLocation") String location, @RequestParam("participantCount") int participantCount,
			@RequestParam("recordDescription") String desc, Principal principal, Model model) {

		if (principal != null) {
			String username = principal.getName(); // 로그인된 사용자명

			// UUID 기반 리더 user_id 조회
			BandInsertVo leaderInfo = bandinsertselectservice.selectLeaderInfo(bandId);
			String userId = leaderInfo.getUser_id();

			model.addAttribute("LeaderId", userId); // UUID를 model에 저장 (필요 시 화면 사용)

			// 서비스 호출
			bandinsertselectservice.insertActivityRecord(recordImage, title, date, time, location, participantCount,
					desc, bandId, userId);
		}

		// 리디렉션: 활동 기록 등록 후 상세 페이지 재진입
		return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	
	// 활동 기록 수정
	@PostMapping("/recordupdate")
	public String updateRecord(@RequestParam("recordId") Long recordId,
	                           @RequestParam("bandId") Long bandId,
	                           @RequestParam("recordTitle") String title,
	                           @RequestParam("recordDate") String date,
	                           @RequestParam("recordTime") String time,
	                           @RequestParam("recordLocation") String location,
	                           @RequestParam("participantCount") int participantCount,
	                           @RequestParam("recordDescription") String desc,
	                           @RequestParam(value = "recordImage", required = false) MultipartFile recordImage) {

	    BandActiveInsertVo vo = new BandActiveInsertVo();
	    vo.setRecordId(recordId);
	    vo.setRecordTitle(title);
	    vo.setRecordDate(LocalDate.parse(date));
	    vo.setRecordTime(time);
	    vo.setRecordLocation(location);
	    vo.setParticipantCount(participantCount);
	    vo.setRecordDescription(desc);

	    if (recordImage != null && !recordImage.isEmpty()) {
	        try {
	            String fileName = UUID.randomUUID() + "_" + recordImage.getOriginalFilename();
	            Path path = Paths.get("C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\" + fileName);
	            Files.copy(recordImage.getInputStream(), path);
	            vo.setRecordImageUrl(fileName);
	        } catch (IOException e) {
	            throw new RuntimeException("이미지 저장 실패", e);
	        }
	    }

	    bandinsertselectservice.updateActivityRecord(vo);
	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	// 밴드 프로필 이미지 수정
	@PostMapping("/updateProfileImage")
	public String updateBandProfileImage(@RequestParam("bandId") Long bandId,
	                                     @RequestParam("profileImage") MultipartFile profileImage) {
	    if (profileImage != null && !profileImage.isEmpty()) {
	        try {
	            // 저장 파일명 생성 (UUID + 확장자)
	            String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();

	            // 저장 경로
	            Path savePath = Paths.get("C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\" + fileName);
	            Files.copy(profileImage.getInputStream(), savePath);

	            // DB 업데이트
	            bandinsertselectservice.updateBandProfileImage(bandId, fileName);

	        } catch (IOException e) {
	            throw new RuntimeException("프로필 이미지 저장 실패", e);
	        }
	    }

	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}
	
	// 밴드 커버 이미지 수정
	@PostMapping("/updateCoverImage")
	public String updateBandCoverImage(@RequestParam("bandId") Long bandId,
	                                   @RequestParam("coverFile") MultipartFile coverFile) {
	    if (coverFile != null && !coverFile.isEmpty()) {
	        try {
	            String fileName = UUID.randomUUID() + "_" + coverFile.getOriginalFilename();
	            Path savePath = Paths.get("C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\" + fileName);
	            Files.copy(coverFile.getInputStream(), savePath);

	            bandinsertselectservice.updateBandCoverImage(bandId, fileName); 

	        } catch (IOException e) {
	            throw new RuntimeException("커버 이미지 저장 실패", e);
	        }
	    }

	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	// 밴드 이름 수정
	@PostMapping("/updateBandName")
	public String updateBandName(@RequestParam("bandId") Long bandId,
	                             @RequestParam("bandName") String bandName) {
	    bandinsertselectservice.updateBandName(bandId, bandName);
	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	// 밴드 한 줄 소개 수정
	@PostMapping("/updateBandOneLiner")
	public String updateBandOneLiner(@RequestParam("bandId") Long bandId,
	                                 @RequestParam("bandOneLiner") String oneLiner) {
	    bandinsertselectservice.updateBandOneLiner(bandId, oneLiner);
	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	// 리더 코멘트 수정
	@PostMapping("/updateLeaderComment")
	public String updateLeaderComment(@RequestParam("bandId") Long bandId,
	                                  @RequestParam("leaderComment") String comment) {
	    bandinsertselectservice.updateLeaderComment(bandId, comment);
	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	// 결성일 수정
	@PostMapping("/updateFormationDate")
	@ResponseBody
	public String updateFormationDate(@RequestParam("bandId") Long bandId,
	                                @RequestParam("formationDate") String formationDate) {
		bandinsertselectservice.updateFormationDate(bandId, formationDate);
		return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}
	
	// ✅ 태그 수정 (장르, 포지션, 성별, 연령)
	@PostMapping("/updateBandIntro")
	public String updateBandIntro(@RequestParam("bandId") Long bandId,
	                              @RequestParam(name = "genre", required = false) String genre,
	                              @RequestParam(name = "position", required = false) String position,
	                              @RequestParam(name = "gender", required = false) String gender,
	                              @RequestParam(name = "age", required = false) String age) {

	    if (genre != null) {
	        List<String> genreList = convertToList(genre);
	        bandinsertselectservice.updateBandTags(bandId, "genre", genreList);
	    }
	    if (position != null) {
	        List<String> positionList = convertToList(position);
	        bandinsertselectservice.updateBandTags(bandId, "position", positionList);
	    }
	    if (gender != null) {
	        List<String> genderList = convertToList(gender);
	        bandinsertselectservice.updateBandTags(bandId, "gender", genderList);
	    }
	    if (age != null) {
	        List<String> ageList = convertToList(age);
	        bandinsertselectservice.updateBandTags(bandId, "age", ageList);
	    }

	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}


	// 🔧 문자열을 List<String>으로 변환하는 공통 메서드
	private List<String> convertToList(String csv) {
	    return Arrays.stream(csv.split(","))
	                 .map(String::trim)
	                 .filter(s -> !s.isEmpty())
	                 .distinct()
	                 .collect(Collectors.toList());
	}

	// 활동 지역 수정
	@PostMapping("/updateRegion")
	public String updateRegion(@RequestParam("bandId") Long bandId,
	                           @RequestParam("region") String region) {
	    bandinsertselectservice.updateRegion(bandId, region);
	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}

	// 멤버 소개 수정
	@PostMapping("/updateMemberInfo")
	public String updateMemberInfo(@RequestParam("bandId") Long bandId,
	                               @RequestParam("bandMemberId") Long bandMemberId,
	                               @RequestParam("stage_name") String stage_name,
	                               @RequestParam("member_position") String member_position,
	                               @RequestParam("member_mbti") String member_mbti,
	                               @RequestParam("favorite_band") String favorite_band,
	                               @RequestParam("member_motto") String member_motto,
	                               @RequestParam("member_type") String member_type) {
		
	    BandInsertVo vo = new BandInsertVo();
	    vo.setBand_member_id(bandMemberId);
	    vo.setStage_name(stage_name);
	    vo.setMember_position(member_position);
	    vo.setMember_mbti(member_mbti);
	    vo.setFavorite_band(favorite_band);
	    vo.setMember_motto(member_motto);

	    // Enum 타입은 set하지 말고 문자열로 직접 비교 후 서비스 분기
	    if ("leader".equals(member_type)) {
	        vo.setMember_type(MemberType.leader);
	        bandinsertselectservice.updateLeaderProfile(vo);
	    } else {
	        vo.setMember_type(MemberType.member);
	        bandinsertselectservice.updateMemberProfile(vo);
	    }


	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}


	// 유튜브 링크 수정
	@PostMapping("/updateYoutubeLink")
	public String updateYoutubeLink(@RequestParam("bandId") Long bandId,
	                                @RequestParam("youtubeLink") String youtubeLink) {
	    bandinsertselectservice.updateYoutubeLink(bandId, youtubeLink);
	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}
	
	// 인스타그램 링크 수정
	@PostMapping("/updateInstagramLink")
	public String updateInstagramLink(@RequestParam("bandId") Long bandId,
	                                  @RequestParam("instagramLink") String instagramLink) {
	    bandinsertselectservice.updateInstagramLink(bandId, instagramLink);
	    return "redirect:/bandinsertselect/modify?band_id=" + bandId;
	}


}