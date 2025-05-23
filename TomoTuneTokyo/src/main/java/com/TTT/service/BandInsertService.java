package com.TTT.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TTT.domain.BandInsertVo;
import com.TTT.domain.MemberType;
import com.TTT.mapper.BandInsertMapper;

@Service
public class BandInsertService {

	@Autowired
	private BandInsertMapper bandInsertMapper;

	private final String uploadDir = "C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"; // 이미지 저장 경로
	
	// 밴드 결성 등록
	public void bandInsert(BandInsertVo vo,
	                       MultipartFile bandProfileImage,
	                       MultipartFile bandCoverImage,
	                       List<MultipartFile> photoList,
	                       List<BandInsertVo> generalMemberList,
	                       String selectedGenres,
	                       String selectedPositions,
	                       String selectedGenders,
	                       String selectedAges) {

	    // 1. 밴드 프로필 이미지 저장
	    if (bandProfileImage != null && !bandProfileImage.isEmpty()) {
	        try {
	            String fileName = UUID.randomUUID() + "_" + bandProfileImage.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir + fileName);
	            Files.copy(bandProfileImage.getInputStream(), filePath);
	            vo.setBand_profile_img(fileName);
	        } catch (IOException e) {
	            throw new RuntimeException("밴드 프로필 이미지 저장 실패", e);
	        }
	    }

	    // 2. 밴드 커버 이미지 저장
	    if (bandCoverImage != null && !bandCoverImage.isEmpty()) {
	        try {
	            String fileName = UUID.randomUUID() + "_" + bandCoverImage.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir + fileName);
	            Files.copy(bandCoverImage.getInputStream(), filePath);
	            vo.setBand_cover_img(fileName);
	        } catch (IOException e) {
	            throw new RuntimeException("밴드 커버 이미지 저장 실패", e);
	        }
	    }

	    // 3. 생성일자 세팅
	    vo.setCreated_at(LocalDateTime.now());

	    // 4. 밴드 기본 정보 등록 → band_id 생성됨
	    bandInsertMapper.InsertBandInfo(vo);
	    Long bandId = vo.getBand_id();

	    // 5. 장르 선택
	    String[] types = {"genre", "position", "gender", "age"};
	    String[] values = {vo.getSelectedGenres(), vo.getSelectedPositions(), vo.getSelectedGenders(), vo.getSelectedAges()};

	    for (int i = 0; i < types.length; i++) {
	        if (values[i] == null || values[i].isBlank()) continue;

	        String[] splitTags = values[i].split(",");
	        for (String tag : splitTags) {
	            if (tag.trim().isEmpty()) continue;

	            BandInsertVo tagVo = new BandInsertVo();
	            tagVo.setBand_id(bandId);
	            tagVo.setTag_type(types[i]);
	            tagVo.setTag_value(tag.trim());

	            bandInsertMapper.InsertBandTag(tagVo);
	        }
	    }


	    // 6. 리더 사진 저장 (photoList[0])
	    if (photoList != null && photoList.size() > 0 && !photoList.get(0).isEmpty()) {
	        try {
	            MultipartFile leaderPhoto = photoList.get(0);
	            String fileName = UUID.randomUUID() + "_" + leaderPhoto.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir + fileName);
	            Files.copy(leaderPhoto.getInputStream(), filePath);
	            vo.setPhoto(fileName);
	        } catch (IOException e) {
	            throw new RuntimeException("리더 사진 저장 실패", e);
	        }
	    } else {
	        vo.setPhoto(null); // 또는 기본 이미지 처리
	    }

	 // 리더도 대문자로 변환
	    if (vo.getMember_mbti() != null) {
	        vo.setMember_mbti(vo.getMember_mbti().toUpperCase());
	    }
	    System.out.println("📦 리더 MBTI 길이: " + (vo.getMember_mbti() != null ? vo.getMember_mbti().length() : "null"));
	    System.out.println("📦 리더 MBTI 값: [" + vo.getMember_mbti() + "]");

	    // 7. 리더 등록
	    vo.setBand_id(bandId);
	    vo.setMember_type(MemberType.leader);
	    bandInsertMapper.InsertBandMember(vo);

	    // 8. 일반 멤버 등록
	    System.out.println("📦 일반 멤버 등록 시작: " + generalMemberList.size() + "명");
	    for (int i = 0; i < generalMemberList.size(); i++) {
	        BandInsertVo member = generalMemberList.get(i);
	        member.setBand_id(bandId);
	        member.setMember_type(MemberType.member);
	        member.setCreated_at(LocalDateTime.now());
	        
	        if (member.getMember_mbti() != null) {
	            member.setMember_mbti(member.getMember_mbti().toUpperCase());
	        }
	        
	        System.out.println("🔍 멤버 " + (i + 1) + " user_id: " + member.getUser_id());
		    System.out.println("🔍 멤버 " + (i + 1) + " stage_name: " + member.getStage_name());

	        // 안전하게 photoList 인덱스 접근 (photoList[1]부터)
	        if (photoList != null && photoList.size() > i + 1 && !photoList.get(i + 1).isEmpty()) {
	            try {
	                MultipartFile photo = photoList.get(i + 1);
	                String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
	                Path filePath = Paths.get(uploadDir + fileName);
	                Files.copy(photo.getInputStream(), filePath);
	                member.setPhoto(fileName);
	            } catch (IOException e) {
	                throw new RuntimeException("멤버 " + (i + 1) + " 사진 저장 실패", e);
	            }
	        } else {
	            member.setPhoto(null); // 또는 기본 이미지 처리
	        }

	        bandInsertMapper.InsertBandMember(member);
	    }
	   
	}


	// 밴드 결성 입력폼 리더
	// ✅ 2-1. username을 기반으로 user_id(UUID) 조회
	public String findUserIdByUsername(String username) {
		return bandInsertMapper.findUserIdByUsername(username);
	}

	// ✅ 2-2. user_id(UUID)로 user_profile 정보 조회
	public BandInsertVo getMyProfile(String userId) {
		return bandInsertMapper.getMyProfile(userId);
	}

	// 밴드 결성 입력폼 일반 멤버 검색 기능
	public List<BandInsertVo> getmembersSelect(String keyword, String excludeUserId) {
		return bandInsertMapper.searchMembersNickname(keyword, excludeUserId);
	}

	// 밴드 결성 입력폼 일반 멤버 검색 리스트
	public List<BandInsertVo> selectAllMembersSelect(String excludeUserId) {
		return bandInsertMapper.selectAllMembersSelect(excludeUserId);
	}

	// 결성된 밴드를 list에서 조회
	public List<BandInsertVo> getAllBands() {
		return bandInsertMapper.selectAllBands();
	}
	
	// 필터 조건에 맞는 밴드를 list에서 조회
	public List<BandInsertVo> getBandsByConditions(String genre, String position, String gender, String age) {
	    return bandInsertMapper.selectBandsByConditions(genre, position, gender, age);
	}
	
	// band_id 호출
	public Long getBandIdByBandName(String band_name) {
		return bandInsertMapper.FindBandIdByBandId(band_name);
	}

	// 밴드명 검색창 검색
	public List<BandInsertVo> searchByTeamNameOrPosition(String keyword) {
	    return bandInsertMapper.searchBandsByName(keyword);
	}
	
	



}
