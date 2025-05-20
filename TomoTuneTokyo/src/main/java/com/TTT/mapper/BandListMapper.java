package com.TTT.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.TTT.domain.BandListInsertVo;



@Mapper
public interface BandListMapper {

	// 구인구직 insert
	@Insert("INSERT INTO posts (post_id, board_id, user_id, post_title, post_content, post_img, post_status, post_like, created_at) VALUES (#{post_id}, #{board_id}, #{user_id}, #{post_title}, #{post_content}, #{post_img}, #{post_status}, #{post_like}, NOW())")
    void BandMemeberInsert(BandListInsertVo vo);
	
	
	
	
	
	
	}
