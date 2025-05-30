package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.BandRecruitPostVo;

@Mapper
// 밴드 구인구직 list
public interface BandRecruitPostMapper {

	// username으로 user_id(UUID) 조회
	@Select("SELECT user_id FROM users WHERE username = #{username}")
	String findUserIdByUsername(@Param("username") String username);

	// 밴드 구인구직 insert
	@Insert("INSERT INTO band_recruit_post (band_id, user_id, band_intro, title, recruit_position, activity_area, recruit_condition, preferred_genres, leader_comment, deadline, tag_keywords, image1_url, image2_url, image3_url, image4_url, created_at, updated_at) " +
	        "VALUES (#{band_id}, #{userId}, #{band_intro}, #{title}, #{recruitPosition}, #{activityArea}, #{recruitCondition}, #{preferredGenres}, #{leaderComment}, #{deadline}, #{tagKeywords}, #{image1Url}, #{image2Url}, #{image3Url}, #{image4Url}, NOW(), NOW())")
	@Options(useGeneratedKeys = true, keyProperty = "post_id")
	void insertBandRecruitPost(BandRecruitPostVo vo);


	// 구인구직 추천 태그 insert
	@Insert("INSERT INTO recruit_tags (post_id, tag_type, tag_value) VALUES (#{post_id}, #{tag_type}, #{tag_value})")
	void insertRecruitTag(BandRecruitPostVo vo);
	
	// band_id 조회
	@Select("SELECT band_id FROM band_member WHERE user_id = #{userId} AND member_type = 'LEADER'")
	List<Long> findBandIdsByUserId(@Param("userId") String userId);

	// 밴드 리스트 전체 조회 (리더용)
	@Select("SELECT b.band_id, b.band_name FROM bands b INNER JOIN band_member bm ON b.band_id = bm.band_id WHERE bm.user_id = #{userId} AND bm.member_type = 'LEADER'")
	List<BandRecruitPostVo> getBandsByUserId(@Param("userId") String userId);

	// 구인구직 글 조회
	@Select("SELECT post_id, title, band_id, band_intro, image1_url AS image1Url, created_at FROM band_recruit_post ORDER BY created_at DESC")
	List<BandRecruitPostVo> selectAllRecruitPosts();

	// 전체 게시글 수 조회
	@Select("SELECT COUNT(*) FROM band_recruit_post")
	int countRecruitPosts();

	// 페이징된 리스트 조회
	@Select("SELECT post_id, title, band_id, band_intro, image1_url AS image1Url, created_at FROM band_recruit_post ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
	List<BandRecruitPostVo> getRecruitPostsByPage(@Param("limit") int limit, @Param("offset") int offset);

	// 밴드 팀명 검색창
	@Select("SELECT post_id, band_id, user_id, band_intro, title, recruit_position, activity_area, recruit_condition, preferred_genres, leader_comment, deadline, tag_keywords, image1_url AS image1Url, created_at, updated_at FROM band_recruit_post WHERE band_id IN (SELECT band_id FROM bands WHERE band_name LIKE CONCAT('%', #{keyword}, '%')) ORDER BY created_at DESC")
	List<BandRecruitPostVo> searchBandsByName(@Param("keyword") String keyword);
	
	// bnad_name 불러오기
	@Select("SELECT band_name FROM bands WHERE band_id = #{bandId}")
	String findBandNameById(@Param("bandId") Long bandId);





}
