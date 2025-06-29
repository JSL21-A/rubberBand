package com.TTT.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import com.TTT.domain.BandInsertVo;
import com.TTT.service.BandInsertService;
import com.TTT.service.NotificationService;

// 밴드 결성 관련 콘트롤러 
@Controller
@RequestMapping("/bandinsert")
public class BandInsertController {
	
	@Autowired
	private BandInsertService bandInsertService;
	
	@Autowired
	NotificationService notificationService;
	
	// 밴드 list
	@GetMapping("/modifylist")
	public String modifylist(@RequestParam(value = "genre", required = false) String genre,
	                         @RequestParam(value = "position", required = false) String position,
	                         @RequestParam(value = "gender", required = false) String gender,
	                         @RequestParam(value = "age", required = false) String age,
	                         @RequestParam(value = "keyword", required = false) String keyword,
	                         @RequestParam(value = "page", defaultValue = "1") int page,
	                         @RequestParam(value = "size", defaultValue = "6") int size,
	                         Model model, Principal principal) {

		 if (page < 1) page = 1;
		 if (size < 1) size = 6;
		    
	    // 페이징 처리에 필요한 start 값 계산
	    int start = (page - 1) * size;

	    // 조건이 모두 비어있을 때만 전체 리스트 페이징 조회 실행
	    boolean isNoFilter = (genre == null || genre.isBlank())
	                      && (position == null || position.isBlank())
	                      && (gender == null || gender.isBlank())
	                      && (age == null || age.isBlank())
	                      && (keyword == null || keyword.isBlank());

	    if (isNoFilter) {
	        List<BandInsertVo> pagedList = bandInsertService.getAllBandsWithPaging(genre, position, gender, age, keyword, start, size);
	        int totalCount = bandInsertService.countAllBands(genre, position, gender, age, keyword);
	        int totalPages = (int) Math.ceil((double) totalCount / size);

	        model.addAttribute("bandList", pagedList);
	        model.addAttribute("page", page);
	        model.addAttribute("totalPages", totalPages);
	    } else {
	        // 필터 조건에 따라 밴드 리스트 조회 (페이징 없이)
	        List<BandInsertVo> bandList = bandInsertService.getBandsByConditions(genre, position, gender, age);
	        model.addAttribute("bandList", bandList);
	    }

	    // 검색어로 밴드명 검색한 결과 (검색창 우선)
	    List<BandInsertVo> bandnamelist = bandInsertService.searchByTeamNameOrPosition(keyword);
	    model.addAttribute("bandnamelist", bandnamelist);

	    // 로그인한 리더 정보 전달
	    if (principal != null) {
	        String username = principal.getName();
	        String userId = bandInsertService.findUserIdByUsername(username);
	        BandInsertVo leaderInfo = bandInsertService.getMyProfile(userId);
	        model.addAttribute("leaderInfo", leaderInfo);
	    }

	    // 선택된 필터 값 View에 다시 전달 (토글 유지용)
	    model.addAttribute("selectedGenre", genre);
	    model.addAttribute("selectedPosition", position);
	    model.addAttribute("selectedGender", gender);
	    model.addAttribute("selectedAge", age);
	    model.addAttribute("keyword", keyword);

	    return "band/modifylist";
	}

	
	// 밴드 결성 입력폼 (저장 기능 포함)
	 @PostMapping("/bandsinsert")
	   public String insert(
	       BandInsertVo vo,
	       @RequestParam("bandProfileImage") MultipartFile bandProfileImage,  // 프로필 이미지 처리
	       @RequestParam("bandCoverImage") MultipartFile bandCoverImage,      // 커버 이미지 처리
	       // 멤버 이미지 관련 파라미터 제거
	       @RequestParam("stage_name") List<String> stageNames,
	       @RequestParam("member_position") List<String> positions,
	       @RequestParam("member_mbti") List<String> mbtis,
	       @RequestParam("favorite_band") List<String> favoriteBands,
	       @RequestParam("member_motto") List<String> mottos,
	       @RequestParam("user_id_list") List<String> userIdList,
	       @RequestParam("selectedGenres") String selectedGenres,
	       @RequestParam("selectedPositions") String selectedPositions,
	       @RequestParam("selectedGenders") String selectedGenders,
	       @RequestParam("selectedAges") String selectedAges,
	       Principal principal
	   ) {
	       // 1. 로그인된 사용자 UUID (user_id) 가져오기
	       String username = principal.getName();
	       String userId = bandInsertService.findUserIdByUsername(username);
	       vo.setUser_id(userId);
	       System.out.println("🎯 리더 user_id: " + vo.getUser_id());

	       // 2. 리더 기본 정보 처리
	       if (!stageNames.isEmpty()) vo.setStage_name(stageNames.get(0));
	       if (!positions.isEmpty()) vo.setMember_position(positions.get(0));
	       if (!mbtis.isEmpty()) {
	           String mbti = mbtis.get(0).trim().toUpperCase();
	           vo.setMember_mbti(mbti.length() > 4 ? mbti.substring(0, 4) : mbti);
	       }
	       if (!favoriteBands.isEmpty()) vo.setFavorite_band(favoriteBands.get(0));
	       if (!mottos.isEmpty()) vo.setMember_motto(mottos.get(0));

	       // 3. 리더 프로필 이미지
	       if (bandProfileImage != null && !bandProfileImage.isEmpty()) {
	           String leaderFileName = UUID.randomUUID() + "_" + bandProfileImage.getOriginalFilename();
	           vo.setBand_profile_img(leaderFileName);
	       } else {
	           vo.setBand_profile_img(null);
	       }

	       // 4. 커버 이미지
	       if (bandCoverImage != null && !bandCoverImage.isEmpty()) {
	           String coverFileName = UUID.randomUUID() + "_" + bandCoverImage.getOriginalFilename();
	           vo.setBand_cover_img(coverFileName);
	       } else {
	           vo.setBand_cover_img(null);
	       }
	       

	       // 5. 일반 멤버 리스트 처리
	       List<BandInsertVo> generalMemberList = new ArrayList<>();
	       
	       for (int i = 0; i < userIdList.size(); i++) {
	           BandInsertVo member = new BandInsertVo();
	          // UUID 변환 로직
	           String rawUserId = userIdList.get(i);
	           String convertedUserId = rawUserId;

	           // UUID가 아니면 닉네임 → UUID 변환 (UUID가 U로 시작해서 Nickname으로 자꾸 변환이 되서 추가함)
	           if (rawUserId != null && rawUserId.startsWith("U") && rawUserId.length() == 33) {
	               convertedUserId = rawUserId;
	           } else {
	               convertedUserId = bandInsertService.findUserIdByUsername(rawUserId);  // 닉네임일 경우만
	           }

	           member.setUser_id(convertedUserId);
	           
	           // 활동명(stage_name)
	           if (stageNames.size() > i + 1) {
	               String stage = stageNames.get(i + 1);
	               if (stage != null && !stage.trim().isEmpty()) {
	                   member.setStage_name(stage);
	               } else {
	                   System.out.println("⚠️ 일반 멤버 " + (i + 1) + " 활동명 비어 있음!");
	               }
	           } else {
	               System.out.println("❌ 일반 멤버 " + (i + 1) + "의 stage_name 누락됨 (index 초과)");
	           }

	           // 포지션 (position)
	           if (positions.size() > i + 1) member.setMember_position(positions.get(i + 1));
	           
	           // mbti
	           if (mbtis.size() > i + 1) {
	               String mbti = mbtis.get(i + 1).trim().toUpperCase();
	               member.setMember_mbti(mbti.length() > 4 ? mbti.substring(0, 4) : mbti);
	           }
	           
	           // 좋아하는 밴드 (favorte_band)
	           if (favoriteBands.size() > i + 1) {
	               String band = favoriteBands.get(i + 1);
	               System.out.println("📌 일반 멤버 " + (i + 1) + " favorite_band 값: " + band);
	               member.setFavorite_band(band);
	           } else {
	               System.out.println("⚠️ 일반 멤버 " + (i + 1) + "의 favorite_band 누락 또는 인덱스 초과");
	           }

	           // 하고 싶은 말 (motto)
	           if (mottos.size() > i + 1) member.setMember_motto(mottos.get(i + 1));

	           
	           // 멤버 이미지 관련 코드 제거됨
	           

	           generalMemberList.add(member);
	           
	           
	       }

	       // 6. 서비스 호출
	       bandInsertService.bandInsert(vo, bandProfileImage, bandCoverImage, generalMemberList, selectedGenres, selectedPositions, selectedGenders, selectedAges);

	       return "redirect:/bandinsert/modifylist";
	   }

		
		// 리더 검색 (Principal를 사용해서 user_id 가져옴)
		@GetMapping("/membersearch/leader")
		@ResponseBody
		public BandInsertVo getLeaderProfile(Principal principal) {
		    String userId = principal.getName(); // 문자열 그대로 사용
		    return bandInsertService.getMyProfile(userId); // 아래 서비스도 String 기반으로 수정되어 있어야 함
		}


		// 일반 멤버 검색 (nickname으로 검색)
		@GetMapping("/membersearch/general")
		@ResponseBody
		public List<BandInsertVo> searchGeneralMembers(@RequestParam("keyword") String keyword) {
		    return bandInsertService.getmembersSelect(keyword, keyword);
		}
		
		// 일반 멤버 전체 리스트
		@GetMapping("/membersearch/allmember")
		@ResponseBody
		public List<BandInsertVo> selectAllMembersSelect(Principal principal) {
		    String username = principal.getName(); // 로그인 ID (username)
		    String userId = bandInsertService.findUserIdByUsername(username); // username -> user_id 변환
		    System.out.println("제외할 user_id: " + userId);
		    return bandInsertService.selectAllMembersSelect(userId);
		}


		// band_id 불러오기
		@GetMapping("/bandinsert/findBandId")
		@ResponseBody
		public Long getBandId(@RequestParam String band_name, Principal principal) {
		    String userId = principal.getName(); // 로그인된 사용자 ID
		    System.out.println("요청자 user_id: " + userId);
		    return bandInsertService.getBandIdByBandName(band_name); // band_name으로 band_id 조회
		}

	
}
