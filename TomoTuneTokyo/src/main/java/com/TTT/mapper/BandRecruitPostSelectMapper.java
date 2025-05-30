package com.TTT.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;

import com.TTT.domain.BandRecruitPostVo;

@Mapper
// 밴드 구인구직 상세보기
public interface BandRecruitPostSelectMapper {

	// post_id 불러오기
	@Select("SELECT post_id, band_id, user_id, band_intro, title, recruit_position AS recruitPosition, activity_area AS activityArea, recruit_condition AS recruitCondition, preferred_genres AS preferredGenres, leader_comment AS leaderComment, deadline, image1_url AS image1Url, image2_url AS image2Url, image3_url AS image3Url, image4_url AS image4Url, created_at AS createdAt, updated_at AS updatedAt "
			+ "FROM band_recruit_post WHERE post_id = #{postId}")
	BandRecruitPostVo getPostById(Long postId);

	// band_name
	@Select("SELECT band_name FROM bands WHERE band_id = #{bandId}")
	String selectBandName(@Param("bandId") Long bandId);

	// 조회수 증가
	@Update("UPDATE band_recruit_post SET bandrecruitpost_count = bandrecruitpost_count + 1 WHERE post_id = #{postId}")
	int incrementViewCount(Long postId); // 반환값 int로 수정 (업데이트된 행의 수)

	// 조회수 조회
	@Select("SELECT bandrecruitpost_count FROM band_recruit_post WHERE post_id = #{postId}")
	Long getViewCount(Long postId);

	// 추천 태그 조회s
	@Select("SELECT tag_keywords FROM band_recruit_post WHERE post_id = #{postId}")
	String getTagKeywords(Long postId);

	// 최신 RESUME_ID 1개 조회 (UUID 기준)
	@Select("SELECT resume_id FROM resume WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT 1")
	Long getLatestResumeIdByUserId(@Param("userId") String userId);
	
	// 등록된 모든 RESUME_ID 리스트 조회 (UUID 기준)
	@Select("SELECT resume_id FROM resume WHERE user_id = #{userId} ORDER BY created_at DESC")
	List<Long> getAllResumeIdsByUserId(@Param("userId") String userId);

	// username으로 user_id(UUID) 조회
	@Select("SELECT user_id FROM users WHERE username = #{username}")
	String findUserIdByUsername(@Param("username") String username);

	// 지원하기 post_id가 param 인식이 안되서 우회로 작성
	@Insert("INSERT INTO post_apply (post_id, user_id, resume_id, band_id, created_at) " +
	        "VALUES (#{postId}, #{userId}, #{resumeId}, #{bandId}, NOW())")
	void insertApplication(Map<String, Object> params);
	
	// 지원 중복 방지 (한 band_id 당 한 번만 지원가능)
	@Select("SELECT COUNT(*) FROM post_apply WHERE post_id = #{postId} AND user_id = #{userId}")
	int countExistingApplication(Map<String, Object> params);



	
}
