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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TTT.domain.BandInsertVo;
import com.TTT.service.BandInsertService;


@Controller
@RequestMapping("/bandinsert")
public class BandInsertController {
	
	@Autowired
	private BandInsertService bandInsertService;
	
	// 밴드 list
	@GetMapping("/modifylist")
	public String modifylist(@RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "age", required = false) String age,
            @RequestParam(value = "keyword", required = false) String keyword,
	                         Model model, Principal principal) {

	    // ✅ 필터 조건에 따라 밴드 리스트 조회
	    List<BandInsertVo> bandList = bandInsertService.getBandsByConditions(genre, position, gender, age);
	    model.addAttribute("bandList", bandList);
	    
	    List<BandInsertVo> bandnamelist = bandInsertService.searchByTeamNameOrPosition(keyword);
	    model.addAttribute("bandnamelist", bandnamelist);

	    // ✅ 로그인한 리더 정보 전달
	    if (principal != null) {
	        String username = principal.getName();
	        String userId = bandInsertService.findUserIdByUsername(username);
	        BandInsertVo leaderInfo = bandInsertService.getMyProfile(userId);
	        model.addAttribute("leaderInfo", leaderInfo);
	    }

	    // ✅ 선택된 필터 값 View에 다시 전달 (토글 유지용)
	    model.addAttribute("selectedGenre", genre);
	    model.addAttribute("selectedPosition", position);
	    model.addAttribute("selectedGender", gender);
	    model.addAttribute("selectedAge", age);
	    
	    model.addAttribute("keyword", keyword); // 검색창 view에 다시 전달

	    return "band/modifylist";
	}
	
	
	// 밴드 결성 입력폼 (저장 기능 포함)
	    @InitBinder
	    public void initBinder(WebDataBinder binder) {
	        binder.setDisallowedFields("photo"); // vo.photo는 수동으로 처리
	    }

	    @PostMapping("/bandsinsert")
	    public String insert(
	        BandInsertVo vo,
	        @RequestParam("bandProfileImage") MultipartFile bandProfileImage,
	        @RequestParam("bandCoverImage") MultipartFile bandCoverImage,
	        @RequestParam("photo[]") List<MultipartFile> photoList,
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
	        if (photoList != null && photoList.size() > 0 && !photoList.get(0).isEmpty()) {
	            String leaderFileName = UUID.randomUUID() + "_" + photoList.get(0).getOriginalFilename();
	            vo.setPhoto(leaderFileName);
	        } else {
	            vo.setPhoto(null);
	        }

	        // 4. 일반 멤버 리스트 처리
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

	            // 프로필 이미지
	            int photoIndex = i + 1;
	            if (photoList.size() > photoIndex && photoList.get(photoIndex) != null && !photoList.get(photoIndex).isEmpty()) {
	                String memberFileName = UUID.randomUUID() + "_" + photoList.get(photoIndex).getOriginalFilename();
	                member.setPhoto(memberFileName);
	            } else {
	                member.setPhoto(null);
	            }

	            generalMemberList.add(member);
	        }
	    
	        // 5. 서비스 호출
	        bandInsertService.bandInsert(vo, bandProfileImage, bandCoverImage, photoList, generalMemberList, selectedGenres, selectedPositions, selectedGenders, selectedAges);

	        return "redirect:/bandinsert/modifylist";
	    }

		
		// 리더 검색 (Principal를 사용해서 user_id 가져옴)
		@GetMapping("/membersearch/leader")
		@ResponseBody
		public BandInsertVo getLeaderProfile(Principal principal) {
		    String userId = principal.getName(); // ✅ 문자열 그대로 사용
		    return bandInsertService.getMyProfile(userId); // ✅ 아래 서비스도 String 기반으로 수정되어 있어야 함
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
		    String excludeUserId = principal.getName(); // 현재 로그인한 사용자 ID
		    System.out.println("제외할 user_id: " + excludeUserId);
		    return bandInsertService.selectAllMembersSelect(excludeUserId);
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
