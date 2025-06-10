package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.TTT.domain.BandHistoryDto;
import com.TTT.domain.BandInsertVo;
import com.TTT.domain.MyActiveDto;
import com.TTT.domain.MypageDto;
import com.TTT.domain.PostVo;
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
	MypageDto findById(int resume_id);

	// 이력서 수정
	void updateResume(MypageDto mypageDto);

	// 프로필 조회
	UserProfileDto selectUserProfileByUserId(String userId);

	// 프로필 업데이트
	void updateUserProfile(UserProfileDto userProfileDto);

	// 닉네임 중복 여부 체크
	// Mapper 인터페이스
	int countByNickname(String nickname);

	// MypageMapper.java
	List<MyActiveDto> findCommentsByUserId(String userId);

	// user_id로 소속 밴드(가장 최근 밴드 하나만) 정보 불러오기(영배 작업)
	@Select("SELECT " + "        m.band_member_id, " + "        m.band_id, " + "        m.user_id, "
			+ "        m.member_type, " + "        m.stage_name, " + "        m.member_position, "
			+ "        m.member_mbti, " + "        m.favorite_band, " + "        m.member_motto, "
			+ "        m.created_at, " + "        m.status, " + "        b.band_name " + "      FROM band_member m "
			+ "      JOIN bands b " + "        ON m.band_id = b.band_id " + "      WHERE m.user_id     = #{user_id} "
			+ "        AND m.status      = 'A' " + "      ORDER BY m.band_id  DESC " + "      LIMIT 1")
	BandInsertVo findMyBand(@Param("user_id") String user_id);

	// 마이페이지 밴드 탈퇴(영배 작업)
	@Update("update band_member set status = 'D' where user_id = #{user_id} and band_id = #{band_id}")
	void leaveMyBand(@Param("user_id") String user_id, @Param("band_id") Long band_id);

	// 댓글 작성글 이동
	PostVo findPostById(Long postId);

	// 비밀번호 조회
	String getPasswordByUserId(String userId);

	// 비밀번호 변경
	int updatePassword(@Param("userId") String userId, @Param("password") String password);

	// 내가 쓴 게시글 목록
	List<PostVo> getMyPosts(@Param("userId") String userId);

	// 내가 쓴 댓글 목록
	List<MyActiveDto> getMyComments(@Param("userId") String userId);

	// 사용자의 댓글 ID 목록 삭제
	int deleteCommentsByIds(@Param("userId") String userId, @Param("commentIds") List<Long> commentIds);

	// 댓글 소유자 확인용
	String findCommentOwner(@Param("commentId") Long commentId);
	
	//게시글 삭제
	int deletePostsByIds(@Param("userId") String userId, @Param("postIds") List<Long> postIds);

	// 지원현황(리더)
	List<MyActiveDto> findApplyStatusByWriter(@Param("userId") String userId);




}
