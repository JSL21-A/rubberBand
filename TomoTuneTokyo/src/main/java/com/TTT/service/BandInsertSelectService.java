package com.TTT.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TTT.domain.BandActiveInsertVo;
import com.TTT.domain.BandInsertVo;
import com.TTT.mapper.BandInsertSelectMapper;

// 밴드 결성 후 밴드 상세 조회
@Service
public class BandInsertSelectService {

	@Autowired
	private BandInsertSelectMapper bandinsertselectMapper;

	private final String uploadDir = "C:\\Users\\LG gram\\git\\rubberBand\\TomoTuneTokyo\\src\\main\\resources\\static\\images\\uploads\\bands\\"; // 이미지
																																					// 저장
																																					// 경로

	// 밴드 기본 정보 조회
	public BandInsertVo getBandDetail(Long bandId) {
		return bandinsertselectMapper.selectBandDetail(bandId);
	}

	// 밴드 멤버 정보 조회
	public List<BandInsertVo> getBandMembers(Long bandId) {
		return bandinsertselectMapper.selectBandMembers(bandId);
	}

	// 밴드 태그 정보 조회
	public List<BandInsertVo> getBandTags(Long bandId) {
		return bandinsertselectMapper.selectBandTags(bandId);
	}

	// 리더 이메일 조회
	public String getLeaderEmail(Long bandId) {
		String userId = bandinsertselectMapper.selectLeaderUserId(bandId);
		return bandinsertselectMapper.selectEmailByUserId(userId);
	}

	// 리더 정보 조회
	public BandInsertVo selectLeaderInfo(Long bandId) {
		return bandinsertselectMapper.selectLeaderInfo(bandId);
	}

	// 활동 사진 조회
	public List<BandActiveInsertVo> getBandActivePhotos(Long bandId) {
		return bandinsertselectMapper.selectBandActivePhotos(bandId);
	}

	// 활동 사진 추가
	public void insertBandGallery(MultipartFile photo, String title, String desc, String youtubeUrl, Long bandId,
			String userId) {
		if (photo != null && !photo.isEmpty()) {
			try {
				String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
				Path filePath = Paths.get(uploadDir + fileName);
				Files.copy(photo.getInputStream(), filePath);

				BandActiveInsertVo vo = new BandActiveInsertVo();
				vo.setBandId(bandId);
				vo.setUserId(userId);
				vo.setImageUrl(fileName);
				vo.setActivityTitle(title);
				vo.setActivityContent(desc);
				vo.setActivityYoutubeUrl(youtubeUrl);
				vo.setCreatedAt(LocalDateTime.now());

				bandinsertselectMapper.insertBandActivePhoto(vo);
			} catch (IOException e) {
				throw new RuntimeException("활동 사진 저장 실패", e);
			}
		} else {
			throw new IllegalArgumentException("활동 사진이 비어 있습니다.");
		}
	}

	// 현재 로그인 사용자가 해당 밴드의 리더인지 확인
	public boolean isUserLeader(Long bandId, String userId) {
		return bandinsertselectMapper.isLeader(bandId, userId) > 0;
	}
	
	// uuid -> username
	public String findUserIdByUsername(String username) {
		return bandinsertselectMapper.findUserIdByUsername(username);
	}

	// 활동 사진 수정
	public void updateBandActivePhoto(BandActiveInsertVo vo) {
	    bandinsertselectMapper.updateBandActivePhoto(vo);
	}

	// activity_photo_id 조회
	public BandActiveInsertVo getActivityPhotoById(Long id) {
	    return bandinsertselectMapper.selectBandActivePhotoById(id);
	}

	// 활동 기록 추가
	public void insertActivityRecord(MultipartFile recordImage, String title, String date, String time,
	                                 String location, int participantCount, String desc,
	                                 Long bandId, String userId) {
	    if (recordImage != null && !recordImage.isEmpty()) {
	        try {
	            String fileName = UUID.randomUUID() + "_" + recordImage.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir + fileName);
	            Files.copy(recordImage.getInputStream(), filePath);

	            BandActiveInsertVo vo = new BandActiveInsertVo();
	            vo.setBandId(bandId);
	            vo.setUserId(userId);
	            vo.setRecordTitle(title);
	            vo.setRecordDate(LocalDate.parse(date));
	            vo.setRecordTime(time);
	            vo.setRecordLocation(location);
	            vo.setParticipantCount(participantCount);
	            vo.setCurrentCount(0); // 기본값
	            vo.setRecordStatus("예정");
	            vo.setRecordDescription(desc);
	            vo.setRecordImageUrl(fileName);
	            vo.setCreatedAt(LocalDateTime.now());

	            bandinsertselectMapper.insertActivityRecord(vo);
	        } catch (IOException e) {
	            throw new RuntimeException("활동 기록 이미지 저장 실패", e);
	        }
	    } else {
	        throw new IllegalArgumentException("활동 기록 이미지가 비어 있습니다.");
	    }
	}

	// 활동 기록 조회
	public List<BandActiveInsertVo> getBandActivityRecords(Long bandId) {
	    return bandinsertselectMapper.selectBandActivityRecords(bandId);
	}
	
	// 활동 기록 수정
	public void updateActivityRecord(BandActiveInsertVo vo) {
	    bandinsertselectMapper.updateActivityRecord(vo);
	}

	// 밴드 프로필 이미지 수정
	public void updateBandProfileImage(Long bandId, String fileName) {
	    bandinsertselectMapper.updateBandProfileImage(bandId, fileName);
	}

	// 밴드 커버 이미지 수정
	public void updateBandCoverImage(Long bandId, String fileName) {
	    bandinsertselectMapper.updateBandCoverImage(bandId, fileName);
	}
	
	// 밴드 이름 수정
	public void updateBandName(Long bandId, String bandName) {
		bandinsertselectMapper.updateBandName(bandId, bandName);
	}

	// 밴드 한 줄 소개 수정
	public void updateBandOneLiner(Long bandId, String oneLiner) {
	    bandinsertselectMapper.updateBandOneLiner(bandId, oneLiner);
	}

	// 리더 코멘트 수정
	public void updateLeaderComment(Long bandId, String comment) {
	    bandinsertselectMapper.updateLeaderComment(bandId, comment);
	}

	// 결성일 수정
	public void updateFormationDate(Long bandId, String formationDate) {
		bandinsertselectMapper.updateFormationDate(bandId, formationDate);
	}

	// 장르/포지션/성별/연령 태그 수정
	public void updateBandTags(Long bandId, String tagType, List<String> tagValues) {
	    // 기존 태그 삭제
	    bandinsertselectMapper.deleteBandTagsByType(bandId, tagType);

	    // 새 태그 삽입
	    if (tagValues != null && !tagValues.isEmpty()) {
	        for (String tagValue : tagValues) {
	            BandInsertVo vo = new BandInsertVo();
	            vo.setBand_id(bandId);
	            vo.setTag_type(tagType);
	            vo.setTag_value(tagValue);
	            bandinsertselectMapper.insertBandTag(vo);
	        }
	    }
	}
	
	// 활동 지역 수정
	public void updateRegion(Long bandId, String region) {
		bandinsertselectMapper.updateRegion(bandId, region);
	}

	 // 밴드 멤버 수정 (리더)
	 public void updateLeaderProfile(BandInsertVo vo) {
		 bandinsertselectMapper.updateLeaderMemberProfile(vo);
	    }
	 
	// 밴드 멤버 수정 (일반 멤버)
	 public void updateMemberProfile(BandInsertVo vo) {
		 bandinsertselectMapper.updateGeneralMemberProfile(vo);
	    }
    
	 // 유튜브 주소 수정
	 public void updateYoutubeLink(Long bandId, String youtubeLink) {
		 bandinsertselectMapper.updateYoutubeLink(bandId, youtubeLink);
		}
    
	 // 인스타 주소 수정
	 public void updateInstagramLink(Long bandId, String instagramLink) {
		 bandinsertselectMapper.updateInstagramLink(bandId, instagramLink);
		}
	 
	//초대여부 조회
	public BandInsertVo SelectBandMemberByUserId(String user_id, Long band_id) {
		return bandinsertselectMapper.selectBandMemberUserId(user_id, band_id);
		}
	
	//초대 수락
	public void acceptBandInvite(String user_id, Long band_id) {
		bandinsertselectMapper.acceptBandInvite(user_id, band_id);
	}
	
	//초대 거절
	public void declineBandInvite(String user_id, Long band_id) {
		bandinsertselectMapper.declineBandInvite(user_id, band_id);
	}
	
	//리더 유저 아이디
	public String findLeaderId(Long band_id) {
		return bandinsertselectMapper.findLeaderId(band_id);
	}
}
