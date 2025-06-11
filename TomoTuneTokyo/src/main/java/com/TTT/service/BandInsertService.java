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
	
	@Autowired
	NotificationService notificationService;

	private final String uploadDir = "C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"; // 이미지
																																					// 저장
																																					// 경로

	// 밴드 결성 등록
	public void bandInsert(BandInsertVo vo, MultipartFile bandProfileImage, MultipartFile bandCoverImage,
			List<BandInsertVo> generalMemberList, String selectedGenres, String selectedPositions,
			String selectedGenders, String selectedAges) {

		// 밴드 프로필 이미지 저장
		if (bandProfileImage != null && !bandProfileImage.isEmpty()) {
			try {
				String fileName = UUID.randomUUID() + "_" + bandProfileImage.getOriginalFilename();
				Path filePath = Paths.get(uploadDir + fileName);
				Files.copy(bandProfileImage.getInputStream(), filePath);
				vo.setBand_profile_img(fileName); // 저장된 파일명 설정
			} catch (IOException e) {
				throw new RuntimeException("밴드 프로필 이미지 저장 실패", e);
			}
		}

		// 밴드 커버 이미지 저장
		if (bandCoverImage != null && !bandCoverImage.isEmpty()) {
			try {
				String fileName = UUID.randomUUID() + "_" + bandCoverImage.getOriginalFilename();
				Path filePath = Paths.get(uploadDir + fileName);
				Files.copy(bandCoverImage.getInputStream(), filePath);
				vo.setBand_cover_img(fileName); // 저장된 파일명 설정
			} catch (IOException e) {
				throw new RuntimeException("밴드 커버 이미지 저장 실패", e);
			}
		}

		// 생성일자 세팅
		vo.setCreated_at(LocalDateTime.now());

		// 밴드 기본 정보 등록 → band_id 생성됨
		bandInsertMapper.InsertBandInfo(vo);
		Long bandId = vo.getBand_id();

		// 장르 선택 처리
		String[] types = { "genre", "position", "gender", "age" };
		String[] values = { vo.getSelectedGenres(), vo.getSelectedPositions(), vo.getSelectedGenders(),
				vo.getSelectedAges() };

		for (int i = 0; i < types.length; i++) {
			if (values[i] == null || values[i].isBlank())
				continue;

			String[] splitTags = values[i].split(",");
			for (String tag : splitTags) {
				if (tag.trim().isEmpty())
					continue;

				BandInsertVo tagVo = new BandInsertVo();
				tagVo.setBand_id(bandId);
				tagVo.setTag_type(types[i]);
				tagVo.setTag_value(tag.trim());

				bandInsertMapper.InsertBandTag(tagVo);
			}
		}

		// 리더 등록 
		vo.setBand_id(bandId);
		vo.setMember_type(MemberType.leader);
		vo.setStatus("A");
		bandInsertMapper.InsertBandMember(vo);

		// 일반 멤버 등록
		for (int i = 0; i < generalMemberList.size(); i++) {
			BandInsertVo member = generalMemberList.get(i);
			member.setBand_id(bandId);
			member.setMember_type(MemberType.member);
			member.setCreated_at(LocalDateTime.now());
			member.setStatus("I");

			// 멤버 MBTI 대문자 변환 처리
			if (member.getMember_mbti() != null) {
				member.setMember_mbti(member.getMember_mbti().toUpperCase());
			}
			bandInsertMapper.InsertBandMember(member);
	        notificationService.sendNotification(member.getUser_id(), "invite", "バンドから参加のお誘いが届きました", "/bandinsertselect/modify?band_id=" + bandId);

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

	// 장르 필터 조건에 맞는 밴드를 list에서 조회
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

	// 전체 밴드 리스트 페이징 조회 (검색어 및 토글 필터 조건이 모두 비어있을 때만 작동)
	public List<BandInsertVo> getAllBandsWithPaging(String genre, String position, String gender, String age,
			String keyword, int start, int size) {
		return bandInsertMapper.selectAllBandsWithPaging(genre, position, gender, age, keyword, start, size);
	}

	// 전체 밴드 수 조회 (페이징용) - 조건이 모두 비어 있을 때만 작동
	public int countAllBands(String genre, String position, String gender, String age, String keyword) {
		return bandInsertMapper.countAllBands(genre, position, gender, age, keyword);
	}
	


}
