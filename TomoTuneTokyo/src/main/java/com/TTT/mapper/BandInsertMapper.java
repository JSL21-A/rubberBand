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
	
	// user_id(UUID)로 리더 정보 불러오기
	@Select("SELECT user_profile_id, user_id, nickname, user_img FROM user_profile WHERE user_id = #{userId}")
    BandInsertVo getMyProfile(@Param("userId") String userId);

	// username으로 user_id(UUID) 조회
	@Select("SELECT user_id FROM users WHERE username = #{username}")
	String findUserIdByUsername(@Param("username") String username);

    // 밴드 결성 일반 멤버 검색
	@Select("SELECT user_id, nickname, user_img FROM user_profile WHERE nickname LIKE CONCAT('%', #{keyword}, '%') AND user_id != #{excludeUserId}")
	List<BandInsertVo> searchMembersNickname(@Param("keyword") String keyword, @Param("excludeUserId") String excludeUserId);
	
	// 밴드 결성 일반 멤버 전체 조회 (본인을 제외)
	@Select("SELECT user_id, nickname, user_img FROM user_profile WHERE user_id <> #{excludeUserId}")
	List<BandInsertVo> selectAllMembersSelect(@Param("excludeUserId") String excludeUserId);


	// 결성된 밴드 list에서 조회
	@Select("SELECT b.band_id, b.band_name, b.band_intro, b.band_profile_img, b.created_at, m.stage_name AS stage_name FROM bands b JOIN band_member m ON b.band_id = m.band_id WHERE m.member_type = 'LEADER' ORDER BY b.created_at DESC")
	List<BandInsertVo> selectAllBands();

	// band_id 조회
	@Select("SELECT band_id FROM bands WHERE band_name = #{band_name}")
	Long FindBandIdByBandId(String band_name);
	
	// list에서 장르 태그로 검색
	@Select("SELECT b.band_id, b.band_name, b.band_intro, b.band_profile_img, b.created_at, m.stage_name AS stage_name FROM bands b JOIN band_member m ON b.band_id = m.band_id WHERE m.member_type = 'LEADER' AND (#{genre} IS NULL OR #{genre} = '' OR EXISTS (SELECT 1 FROM band_tags WHERE band_id = b.band_id AND tag_type = 'genre' AND tag_value = #{genre})) AND (#{position} IS NULL OR #{position} = '' OR EXISTS (SELECT 1 FROM band_tags WHERE band_id = b.band_id AND tag_type = 'position' AND tag_value = #{position})) AND (#{gender} IS NULL OR #{gender} = '' OR EXISTS (SELECT 1 FROM band_tags WHERE band_id = b.band_id AND tag_type = 'gender' AND tag_value = #{gender})) AND (#{age} IS NULL OR #{age} = '' OR EXISTS (SELECT 1 FROM band_tags WHERE band_id = b.band_id AND tag_type = 'age' AND tag_value = #{age})) ORDER BY b.created_at DESC")
	List<BandInsertVo> selectBandsByConditions(@Param("genre") String genre, @Param("position") String position, @Param("gender") String gender, @Param("age") String age);

	// 밴드 팀명 검색창
	@Select("SELECT * FROM bands WHERE band_name LIKE CONCAT('%', #{keyword}, '%')")
	List<BandInsertVo> searchBandsByName(@Param("keyword") String keyword);

	// 전체 밴드 리스트 페이징 조회 (리더 활동명 포함)
	@Select("SELECT b.band_id, b.band_name, b.band_intro, b.band_profile_img, b.created_at, (SELECT m.stage_name FROM band_member m WHERE m.band_id = b.band_id AND m.member_type = 'LEADER' LIMIT 1) AS stage_name FROM bands b WHERE (#{genre} IS NULL OR #{genre} = '') AND (#{position} IS NULL OR #{position} = '') AND (#{gender} IS NULL OR #{gender} = '') AND (#{age} IS NULL OR #{age} = '') AND (#{keyword} IS NULL OR #{keyword} = '') ORDER BY b.created_at DESC LIMIT #{size} OFFSET #{start}")
	List<BandInsertVo> selectAllBandsWithPaging(@Param("genre") String genre, @Param("position") String position, @Param("gender") String gender, @Param("age") String age, @Param("keyword") String keyword, @Param("start") int start, @Param("size") int size);

	// 전체 밴드 수 조회 (페이징용)
	@Select("SELECT COUNT(*) FROM bands WHERE (#{genre} IS NULL OR #{genre} = '') AND (#{position} IS NULL OR #{position} = '') AND (#{gender} IS NULL OR #{gender} = '') AND (#{age} IS NULL OR #{age} = '') AND (#{keyword} IS NULL OR #{keyword} = '')")
	int countAllBands(@Param("genre") String genre, @Param("position") String position, @Param("gender") String gender, @Param("age") String age, @Param("keyword") String keyword);



	

	}
