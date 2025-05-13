package com.TTT.service;

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
import com.TTT.mapper.BandInsertMapper;

@Service
public class BandInsertService {

	@Autowired
	private BandInsertMapper bandInsertMapper;

	private final String uploadDir = "C:/upload/"; // 이미지 저장 경로
	// 밴드 결성 등록

	public void bandInsert(BandInsertVo vo, MultipartFile bandProfileImage, MultipartFile bandCoverImage) {
	    // 1. 밴드 프로필 이미지 저장
	    if (bandProfileImage != null && !bandProfileImage.isEmpty()) {
	        try {
	            String fileName = UUID.randomUUID() + "_" + bandProfileImage.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir + fileName);
	            Files.copy(bandProfileImage.getInputStream(), filePath);
	            vo.setBand_profile_img(fileName);
	        } catch (Exception e) {
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
	        } catch (Exception e) {
	            throw new RuntimeException("밴드 커버 이미지 저장 실패", e);
	        }
	    }

	    // 3. 생성일자 세팅
	    vo.setCreated_at(LocalDateTime.now());

	    // 4. 밴드 정보 등록 → 이 시점에 band_id가 자동 생성됨
	    bandInsertMapper.InsertBandInfo(vo);
	    Long bandId = vo.getBand_id(); // @Options(useGeneratedKeys = true)로 채워짐

	    // 5. 단일 태그 등록 (tag_type, tag_value가 있을 경우만)
	    if (vo.getTag_type() != null && vo.getTag_value() != null) {
	        BandInsertVo tagVo = new BandInsertVo();
	        tagVo.setBand_id(bandId);
	        tagVo.setTag_type(vo.getTag_type());
	        tagVo.setTag_value(vo.getTag_value());
	        bandInsertMapper.InsertBandTag(tagVo);
	    }

	    // 6. 밴드 멤버 등록
	    vo.setBand_id(bandId);
	    bandInsertMapper.InsertBandMember(vo);
	}

	
	// 밴드 결성 입력폼 검색기능
	public List<BandInsertVo> searchMembersByStageName(String keyword) {
	    return bandInsertMapper.BandInsertstage_nameSelect(keyword);
	}

	// 밴드 결성 입력폼 리더 검색 기능
	public BandInsertVo getLeaderInfo(String userId) {
	    return bandInsertMapper.selectLeaderInfo(userId);
	}

	// 밴드 결성 입력폼 일반 멤버 검색 기능
	public List<BandInsertVo> searchMembersByStageName(String keyword, String excludeUserId) {
	    return bandInsertMapper.searchMembersExcludingSelf(keyword, excludeUserId);
	}


	// 결성된 밴드를 list에서 조회
	public List<BandInsertVo> getAllBands() {
		return bandInsertMapper.selectAllBands();
	}

	// band_id 호출
	public Long getBandIdByBandName(String band_name) {
		return bandInsertMapper.FindBandIdByBandId(band_name);
	}

}
