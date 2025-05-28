package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.TTT.domain.BandHistoryDto;
import com.TTT.domain.MyActiveDto;
import com.TTT.domain.MypageDto;
import com.TTT.domain.UserProfileDto;

@Mapper
public interface MypageMapper {

	// 이력서 작성/수정/조회
	void insertResume(MypageDto mypageDto);

	MypageDto selectResumeByUserId(String userId);

	// 밴드활동이력 insert
	void insertBandHistory(BandHistoryDto bandHistoryDto);

	List<BandHistoryDto> selectBandHistoryByResumeId(int resumeId);

	// 이력서 존재 여부 & 삭제
	int countResumeByUserId(String userId);

	void deleteResumeByUserId(String userId);

	void deleteBandHistoryByResumeId(int resumeId);

	// resume_id로 이력서 조회
		MypageDto findById(Long id);
	
	// 이력서 수정 
	void updateResume(MypageDto mypageDto);
	
	// 프로필 조회 
	UserProfileDto selectUserProfileByUserId(String userId);

	// 프로필 업데이트
	void updateUserProfile(UserProfileDto userProfileDto);
	
	// 닉네임 중복 여부 체크
	// Mapper 인터페이스
	int countByNickname(String nickname);
	//댓글조회
	// MypageMapper.java
	List<MyActiveDto> findCommentsByUserId(String userId);




}
