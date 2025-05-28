package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.TTT.domain.BandActiveInsertVo;
import com.TTT.domain.BandInsertVo;

@Mapper
// 밴드 결성 후 내용 상세보기
public interface BandInsertSelectMapper {

	// 1. 밴드 기본 정보
	@Select("SELECT band_id, band_name, band_intro, formation_date, region, band_profile_img, band_cover_img, youtube_link, instagram_link, leader_comment, like_count, created_at FROM bands WHERE band_id = #{bandId}")
	BandInsertVo selectBandDetail(@Param("bandId") Long bandId);

	// 2. 밴드 멤버 + 프로필 정보
	@Select("SELECT bm.band_member_id, bm.band_id, bm.user_id, bm.member_type, bm.stage_name, bm.member_position, bm.member_mbti, bm.favorite_band, bm.member_motto, bm.photo, bm.created_at, up.nickname, up.user_img FROM band_member bm JOIN user_profile up ON bm.user_id = up.user_id WHERE bm.band_id = #{bandId} and bm.status = 'A' ORDER BY bm.member_type DESC, bm.band_member_id ASC")
	List<BandInsertVo> selectBandMembers(@Param("bandId") Long bandId);

	// 3. 밴드 태그 정보
	@Select("SELECT band_id, tag_type, tag_value FROM band_tags WHERE band_id = #{bandId}")
	List<BandInsertVo> selectBandTags(@Param("bandId") Long bandId);

	// 리더 user_id 불러오기
	@Select("SELECT user_id FROM band_member WHERE band_id = #{bandId} AND member_type = 'LEADER'")
	String selectLeaderUserId(@Param("bandId") Long bandId);

	// 리더 이메일 불러오기
	@Select("SELECT email FROM users WHERE user_id = #{userId}")
	String selectEmailByUserId(@Param("userId") String userId);

	// 리더 insert 정보 불러오기
	@Select("SELECT band_member_id, user_id, stage_name, member_position, member_mbti, favorite_band, member_motto, photo FROM band_member WHERE band_id = #{bandId} AND member_type = 'LEADER'")
	BandInsertVo selectLeaderInfo(@Param("bandId") Long bandId);

	// 활동 사진 조회
	@Select("SELECT activity_photo_id AS activityPhotoId, band_id AS bandId, user_id AS userId, image_url AS imageUrl, activity_youtube_url AS activityYoutubeUrl, activity_title AS activityTitle, activity_content AS activityContent, created_at AS createdAt, updated_at AS updatedAt FROM band_gallery WHERE band_id = #{bandId} ORDER BY created_at DESC")
	List<BandActiveInsertVo> selectBandActivePhotos(@Param("bandId") Long bandId);

	// 활동 사진 추가
	@Insert("INSERT INTO band_gallery (band_id, user_id, image_url, activity_youtube_url, activity_title, activity_content, created_at) VALUES (#{bandId}, #{userId}, #{imageUrl}, #{activityYoutubeUrl}, #{activityTitle}, #{activityContent}, #{createdAt})")
	@Options(useGeneratedKeys = true, keyProperty = "activityPhotoId")
	void insertBandActivePhoto(BandActiveInsertVo vo);

	// 현재 로그인 사용자가 해당 밴드의 리더인지 확인
	@Select("SELECT COUNT(*) FROM band_member WHERE band_id = #{bandId} AND user_id = #{userId} AND member_type = 'LEADER'")
	int isLeader(@Param("bandId") Long bandId, @Param("userId") String userId);

	// username으로 user_id(UUID) 조회
	@Select("SELECT user_id FROM users WHERE username = #{username}")
	String findUserIdByUsername(@Param("username") String username);
	
	// 활동 사진 수정
	@Update("UPDATE band_gallery SET activity_youtube_url = #{activityYoutubeUrl}, activity_title = #{activityTitle}, activity_content = #{activityContent}, image_url = #{imageUrl}, updated_at = NOW() WHERE activity_photo_id = #{activityPhotoId}")
	void updateBandActivePhoto(BandActiveInsertVo vo);
	
	// activity_photo_id 조회
	@Select("SELECT * FROM band_gallery WHERE activity_photo_id = #{id}")
	BandActiveInsertVo selectBandActivePhotoById(Long id);

	// 활동기록 추가
	@Insert("INSERT INTO activity_record (band_id, user_id, record_title, record_date, record_time, record_location, participant_count, current_count, record_status, record_description, record_image_url, created_at, updated_at) VALUES (#{bandId}, #{userId}, #{recordTitle}, #{recordDate}, #{recordTime}, #{recordLocation}, #{participantCount}, #{currentCount}, #{recordStatus}, #{recordDescription}, #{recordImageUrl}, NOW(), NOW())")
	@Options(useGeneratedKeys = true, keyProperty = "recordId")
	void insertActivityRecord(BandActiveInsertVo vo);

	// 활동 기록 조회
	@Select("SELECT record_id AS recordId, band_id AS bandId, user_id AS userId, record_image_url AS recordImageUrl, record_title AS recordTitle, record_date AS recordDate, record_time AS recordTime, record_location AS recordLocation, participant_count AS participantCount, current_count AS currentCount, record_status AS recordStatus, record_description AS recordDescription, created_at AS createdAt, updated_at AS updatedAt FROM activity_record WHERE band_id = #{bandId} ORDER BY record_date DESC")
	List<BandActiveInsertVo> selectBandActivityRecords(@Param("bandId") Long bandId);

	// 활동 기록 수정
	@Update("UPDATE activity_record SET record_title=#{recordTitle}, record_date=#{recordDate}, record_time=#{recordTime}, record_location=#{recordLocation}, participant_count=#{participantCount}, record_description=#{recordDescription}, record_image_url=#{recordImageUrl}, updated_at=NOW() WHERE record_id=#{recordId}")
	void updateActivityRecord(BandActiveInsertVo vo);

	// 밴드 프로필 이미지 수정
	@Update("UPDATE bands SET band_profile_img = #{bandProfileImg}, updated_at = NOW() WHERE band_id = #{bandId}")
	void updateBandProfileImage(@Param("bandId") Long bandId, @Param("bandProfileImg") String bandProfileImg);

	// 밴드 커버 이미지 수정
	@Update("UPDATE bands SET band_cover_img = #{coverImg}, updated_at = NOW() WHERE band_id = #{bandId}")
	void updateBandCoverImage(@Param("bandId") Long bandId, @Param("coverImg") String coverImg);

	// 밴드 이름 수정
	@Update("UPDATE bands SET band_name = #{bandName}, updated_at = NOW() WHERE band_id = #{bandId}")
	void updateBandName(@Param("bandId") Long bandId, @Param("bandName") String bandName);

	// 밴드 한 줄 소개 수정
	@Update("UPDATE bands SET band_intro = #{oneLiner}, updated_at = NOW() WHERE band_id = #{bandId}")
	void updateBandOneLiner(@Param("bandId") Long bandId, @Param("oneLiner") String oneLiner);

	// 리더 코멘트 수정
	@Update("UPDATE bands SET leader_comment = #{comment}, updated_at = NOW() WHERE band_id = #{bandId}")
	void updateLeaderComment(@Param("bandId") Long bandId, @Param("comment") String comment);

	// 결성일 수정
	@Update("UPDATE bands SET formation_date = #{formationDate}, updated_at = NOW() WHERE band_id = #{bandId}")
	void updateFormationDate(@Param("bandId") Long bandId, @Param("formationDate") String formationDate);

	// 특정 타입의 태그 전체 삭제
	@Delete("DELETE FROM band_tags WHERE band_id = #{bandId} AND tag_type = #{tagType}")
	void deleteBandTagsByType(@Param("bandId") Long bandId, @Param("tagType") String tagType);

	// 태그 삽입
	@Insert("INSERT INTO band_tags (band_id, tag_type, tag_value) VALUES (#{band_id}, #{tag_type}, #{tag_value})")
	void insertBandTag(BandInsertVo vo);

	// 활동 지역 수정
	@Update("UPDATE bands SET region = #{region} WHERE band_id = #{bandId}")
	void updateRegion(@Param("bandId") Long bandId, @Param("region") String region);

	// 밴드 멤버 수정 (일반멤버)
	@Update("UPDATE band_member SET member_position = #{member_position}, member_mbti = #{member_mbti}, favorite_band = #{favorite_band}, member_motto = #{member_motto}, updated_at = NOW() WHERE band_member_id = #{band_member_id} AND member_type = 'member'")
	void updateGeneralMemberProfile(BandInsertVo vo);

	// 밴드 멤버 수정 (리더)
	@Update("UPDATE band_member SET stage_name = #{stage_name}, member_position = #{member_position}, member_mbti = #{member_mbti}, favorite_band = #{favorite_band}, member_motto = #{member_motto}, updated_at = NOW() WHERE band_member_id = #{band_member_id} AND member_type = 'leader'")
	void updateLeaderMemberProfile(BandInsertVo vo);

	// 유튜브 주소 수정
	@Update("UPDATE bands SET youtube_link = #{youtubeLink}, updated_at = NOW() WHERE band_id = #{bandId}")
	void updateYoutubeLink(@Param("bandId") Long bandId, @Param("youtubeLink") String youtubeLink);

	// 인스타 주소 수정
	@Update("UPDATE bands SET instagram_link = #{instagramLink}, updated_at = NOW() WHERE band_id = #{bandId}")
	void updateInstagramLink(@Param("bandId") Long bandId, @Param("instagramLink") String instagramLink);

	//초대 여부 조회하기
	@Select("select * from band_member where user_id = #{user_id} and band_id = #{band_id} and status = 'I'")
	BandInsertVo selectBandMemberUserId(@Param("user_id") String user_id, @Param("band_id") Long band_id);
	
	//초대 수락하기
	@Update("update band_member set status = 'A' where user_id = #{user_id} and band_id = #{band_id}")
	void acceptBandInvite(@Param("user_id") String user_id, @Param("band_id") Long band_id);
	
	//초대 거절하기
	@Delete("delete from band_member where user_id = #{user_id} and band_id = #{band_id}")
	void declineBandInvite(@Param("user_id") String user_id, @Param("band_id") Long band_id);
	
	// 리더 user_id 불러오기
	@Select("select user_id from band_member where band_id = #{band_id} and member_type = 'LEADER'")
	String findLeaderId(@Param("band_id") Long band_id);
	
}
