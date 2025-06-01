package com.TTT.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.TTT.domain.BandHistoryDto;
import com.TTT.domain.BandInsertVo;
import com.TTT.domain.MyActiveDto;
import com.TTT.domain.MypageDto;
import com.TTT.domain.PostVo;
import com.TTT.domain.UserDto;
import com.TTT.domain.UserProfileDto;
import com.TTT.mapper.MypageMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class MypageService {

	@Autowired
	private MypageMapper mypageMapper;

	// 이력서 작성 (중복 작성 방지 포함)
	public void saveResume(MypageDto dto) {
		String userId = dto.getUserId();
		if (hasResume(userId)) {
			throw new IllegalStateException("このユーザーはすでに履歴書を作成しています。");
		}

		// 활동가능지역
		if (dto.getRegion() != null && !dto.getRegion().isEmpty()) {
			dto.setArea(String.join(",", dto.getRegion()));
		}

		// 선호음악장르 처리 (etc 포함)
		if (dto.getGenreList() != null && !dto.getGenreList().isEmpty()) {
			List<String> genreList = dto.getGenreList();
			if (genreList.contains("etc") && dto.getGenreEtc() != null && !dto.getGenreEtc().trim().isEmpty()) {
				genreList = genreList.stream().filter(g -> !"etc".equals(g)).collect(Collectors.toList());
				genreList.add(dto.getGenreEtc().trim());
			}
			dto.setGenre(String.join(",", genreList));
		}

		// 포지션 처리 (etc 포함)
		if (dto.getInstrumentList() != null && !dto.getInstrumentList().isEmpty()) {
			List<String> instList = dto.getInstrumentList();
			if (instList.contains("etc") && dto.getInstrumentEtc() != null
					&& !dto.getInstrumentEtc().trim().isEmpty()) {
				instList = instList.stream().filter(i -> !"etc".equals(i)).collect(Collectors.toList());
				instList.add(dto.getInstrumentEtc().trim());
			}
			dto.setInstrument(String.join(",", instList));
		}

		// 연습가능요일 처리
		if (dto.getPracticeDateList() != null && !dto.getPracticeDateList().isEmpty()) {
			String converted = dto.getPracticeDateList().stream().map(this::convertDayToChar)
					.collect(Collectors.joining(","));
			dto.setPracticeDate(converted);
		}

		// 연습시간대 처리
		dto.setPracticeTime(convertPracticeTime(dto.getPracticeTime()));

		// 이력서 insert
		mypageMapper.insertResume(dto);
		int resumeId = (int) dto.getResumeId();

		// 밴드활동이력 추가
		List<BandHistoryDto> bandList = dto.getBandHistoryList();
		if (bandList != null) {
			for (BandHistoryDto bh : bandList) {
				String bandName = bh.getBandName();
				if (bandName == null || bandName.trim().isEmpty())
					continue;

				bh.setResumeId(resumeId);
				mypageMapper.insertBandHistory(bh);
			}
		}
	}

	// 연습시간대 변환
	private String convertPracticeTime(String time) {
		if (time == null)
			return null;
		switch (time) {
		case "morning":
			return "MORNING";
		case "afternoon":
			return "AFTERNOON";
		case "evening":
			return "EVENING";
		case "other":
			return "OTHER";
		default:
			return "_";
		}
	}

	// 요일 변환
	private String convertDayToChar(String day) {
		switch (day) {
		case "月":
			return "MON";
		case "火":
			return "TUE";
		case "水":
			return "WED";
		case "木":
			return "THU";
		case "金":
			return "FRI";
		case "土":
			return "SAT";
		case "日":
			return "SUN";
		default:
			return "_";
		}
	}

	// 이력서 존재 여부 (없으면 작성 / 있으면 보기로 뜨게)
	public boolean hasResume(String userId) {
		return mypageMapper.countResumeByUserId(userId) > 0;
	}

	// 이력서 존재 여부확인
	public MypageDto getResumeByUserId(String userId) {
		MypageDto resume = mypageMapper.selectResumeByUserId(userId); // 기본 resume 조회
		if (resume != null) {
			List<BandHistoryDto> bandList = mypageMapper.selectBandHistoryByResumeId(resume.getResumeId()); // 밴드 이력 추가로
																											// 조회
			resume.setBandHistoryList(bandList);
		}
		return resume;
	}

	// 특정 계정 이력서 조회
	public MypageDto getResumeById(Long id) {
		MypageDto resume = mypageMapper.findById(id);
		if (resume != null) {
			List<BandHistoryDto> bandList = mypageMapper.selectBandHistoryByResumeId(resume.getResumeId());
			resume.setBandHistoryList(bandList);
		}
		return resume;
	}

	// 이력서 삭제
	public boolean deleteResumeByUserId(String userId) {
		try {

			MypageDto resume = mypageMapper.selectResumeByUserId(userId);

			if (resume == null) {
				return false;
			}

			int resumeId = resume.getResumeId();

			mypageMapper.deleteBandHistoryByResumeId(resumeId);
			mypageMapper.deleteResumeByUserId(userId);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void updateResume(MypageDto dto) {
		// 활동가능지역
		if (dto.getRegion() != null && !dto.getRegion().isEmpty()) {
			dto.setArea(String.join(",", dto.getRegion()));
		}

		// 포지션 (etc 포함)
		if (dto.getInstrumentList() != null && !dto.getInstrumentList().isEmpty()) {
			List<String> instList = dto.getInstrumentList();
			if (instList.contains("etc") && dto.getInstrumentEtc() != null
					&& !dto.getInstrumentEtc().trim().isEmpty()) {
				instList = instList.stream().filter(i -> !"etc".equals(i)).collect(Collectors.toList());
				instList.add(dto.getInstrumentEtc().trim());
			}
			dto.setInstrument(String.join(",", instList));
		}

		// 장르 (etc 포함)
		if (dto.getGenreList() != null && !dto.getGenreList().isEmpty()) {
			List<String> genreList = dto.getGenreList();
			if (genreList.contains("etc") && dto.getGenreEtc() != null && !dto.getGenreEtc().trim().isEmpty()) {
				genreList = genreList.stream().filter(g -> !"etc".equals(g)).collect(Collectors.toList());
				genreList.add(dto.getGenreEtc().trim());
			}
			dto.setGenre(String.join(",", genreList));
		}

		// 연습요일
		if (dto.getPracticeDateList() != null && !dto.getPracticeDateList().isEmpty()) {
			String converted = dto.getPracticeDateList().stream().map(this::convertDayToChar)
					.collect(Collectors.joining(","));
			dto.setPracticeDate(converted);
		}

		// 연습 시간대 처리
		dto.setPracticeTime(convertPracticeTime(dto.getPracticeTime()));

		// 밴드이력 갱신을 위해 기존 이력 삭제 후 재삽입
		int resumeId = dto.getResumeId();
		mypageMapper.deleteBandHistoryByResumeId(resumeId);

		List<BandHistoryDto> bandList = dto.getBandHistoryList();
		if (bandList != null) {
			for (BandHistoryDto bh : bandList) {
				if (bh.getBandName() == null || bh.getBandName().trim().isEmpty())
					continue;
				bh.setResumeId(resumeId);
				mypageMapper.insertBandHistory(bh);
			}
		}

		// 최종 이력서 update
		mypageMapper.updateResume(dto);
	}

	// 프로필 조회
	public UserProfileDto getUserProfileByUserId(String userId) {
		return mypageMapper.selectUserProfileByUserId(userId);
	}

	// 프로필 수정 저장
	public void updateUserProfile(UserProfileDto userProfileDto) {
		System.out.println("서비스 - updateUserProfile 호출됨: " + userProfileDto.getNickname());
		mypageMapper.updateUserProfile(userProfileDto);
		System.out.println("서비스 - updateUserProfile 완료");
	}

	// 닉네임 중복 체크
	public boolean isNicknameDuplicate(String nickname) {
		// user_profile 테이블에서 닉네임 존재 여부 체크
		return mypageMapper.countByNickname(nickname) > 0;
	}

	public List<MyActiveDto> getCommentsByUserId(String userId) {
		return mypageMapper.findCommentsByUserId(userId);
	}

	// 게시글 상세 조회 기능 추가
	public PostVo getPostById(Long postId) {
		return mypageMapper.findPostById(postId);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 현재 비밀번호 확인
	public boolean checkCurrentPassword(String userId, String inputPassword) {
		String encodedPw = mypageMapper.getPasswordByUserId(userId);
		boolean result = passwordEncoder.matches(inputPassword, encodedPw);
		return result;
	}

	// 새로운 비밀번호 업데이트
	public boolean updateUserPassword(String userId, String newPassword) {
		String encoded = passwordEncoder.encode(newPassword);
		return mypageMapper.updatePassword(userId, encoded) > 0;
	}
  
 //소속 밴드 조회
	public BandInsertVo findMyBand(String user_id) {
    return mypageMapper.findMyBand(user_id);
  }
	    
	//소속 밴드 탈퇴
	public void leaveMyBand(String user_id, Long band_id) {
	   mypageMapper.leaveMyBand(user_id, band_id);
	}  

}
