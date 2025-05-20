package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.BandInsertVo;

@Mapper
// 밴드 결성 후 내용 상세보기
public interface BandInsertSelectMapper {
	
	  // 1. 밴드 기본 정보
    @Select("SELECT band_id, band_name, band_intro, formation_date, region, band_profile_img, band_cover_img, youtube_link, instagram_link, leader_comment, like_count, created_at FROM bands WHERE band_id = #{bandId}")
    BandInsertVo selectBandDetail(@Param("bandId") String bandId);

    // 2. 밴드 멤버 + 프로필 정보
    @Select("SELECT bm.band_member_id, bm.band_id, bm.user_id, bm.member_type, bm.stage_name, bm.member_position, bm.member_mbti, bm.favorite_band, bm.member_motto, bm.photo, bm.created_at, up.nickname, up.user_img FROM band_member bm JOIN user_profile up ON bm.user_id = up.user_id WHERE bm.band_id = #{bandId} ORDER BY bm.member_type DESC, bm.band_member_id ASC")
    List<BandInsertVo> selectBandMembers(@Param("bandId") String bandId);

    // 3. 밴드 태그 정보
    @Select("SELECT band_id, tag_type, tag_value FROM band_tags WHERE band_id = #{bandId}")
    List<BandInsertVo> selectBandTags(@Param("bandId") String bandId);
    
    // 리더 user_id 불러오기
    @Select("SELECT user_id FROM band_member WHERE band_id = #{bandId} AND member_type = 'LEADER'")
    String selectLeaderUserId(@Param("bandId") String bandId);
    
    // 리더 이메일 불러오기
    @Select("SELECT email FROM users WHERE user_id = #{userId}")
    String selectEmailByUserId(@Param("userId") String userId);

    // 리더 insert 정보 불러오기
    @Select("SELECT user_id, stage_name, member_position, member_mbti, favorite_band, member_motto, photo FROM band_member WHERE band_id = #{bandId} AND member_type = 'LEADER'")
    BandInsertVo selectLeaderInfo(@Param("bandId") String bandId);

	
	}
