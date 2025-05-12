package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.BandInsertVo;


@Mapper
// 밴드 결성 
public interface BandInsertMapper {
	
	// 밴드 멤버 정보 등록
	@Insert("INSERT INTO band_member (band_id, user_id, member_type, stage_name, member_position, member_mbti, favorite_band, member_motto, photo, created_at) VALUES (#{band_id}, #{user_id}, #{member_type}, #{stage_name}, #{member_position}, #{member_mbti}, #{favorite_band}, #{member_motto}, #{photo}, NOW())")
	@Options(useGeneratedKeys = true, keyProperty = "band_member_id") // band_member_id 시퀀스 -> 자동으로 member_id 추가
	void InsertBandMember(BandInsertVo vo);
	
	// 밴드 기본 등록
	@Insert("INSERT INTO bands (band_id, band_name, band_intro, formation_date, region, band_profile_img, band_cover_img, youtube_link, instagram_link, leader_comment, like_count, created_at) VALUES (#{band_id}, #{band_name}, #{band_intro}, #{formation_date}, #{region}, #{band_profile_img}, #{band_cover_img}, #{youtube_link}, #{instagram_link}, #{leader_comment}, #{like_count}, #{created_at})")
	@Options(useGeneratedKeys = true, keyProperty = "band_id")
	void InsertBandInfo(BandInsertVo vo);
	
	// 밴드 결성 장르 등록
	@Insert("INSERT INTO band_tags (band_id, tag_type, tag_value) VALUES (#{band_id}, #{tag_type}, #{tag_value})")
	void InsertBandTag(BandInsertVo vo);
	
	// 활동명으로 검색 (user_id, stage_name, photo 가져옴)
    @Select("SELECT user_id, stage_name, photo FROM resume WHERE stage_name LIKE CONCAT('%', #{keyword}, '%')")
    List<BandInsertVo> BandInsertstage_nameSelect(@Param("keyword") String keyword);
    
    // 밴드 결성 리더 검색
    @Select("SELECT user_id, stage_name, photo FROM resume WHERE user_id = #{userId}")
    BandInsertVo selectLeaderInfo(@Param("userId") String userId);
    
    // 밴드 결성 일반 멤버 검색
    @Select("SELECT user_id, stage_name, photo FROM resume WHERE stage_name LIKE CONCAT('%', #{keyword}, '%') AND user_id != #{excludeUserId}")
    List<BandInsertVo> searchMembersExcludingSelf(@Param("keyword") String keyword, @Param("excludeUserId") String excludeUserId);

	// 결성된 밴드 list에서 조회
	@Select("SELECT * FROM bands ORDER BY created_at DESC")
	List<BandInsertVo> selectAllBands();

	// band_id 조회
	@Select("SELECT band_id FROM bands WHERE band_name = #{band_name}")
	Long FindBandIdByBandId(String band_name);
	

	
	
	
	
	}
