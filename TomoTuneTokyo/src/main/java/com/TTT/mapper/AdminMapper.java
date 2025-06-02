package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.PostVo;
import com.TTT.domain.UserDto;

@Mapper
public interface AdminMapper {

    // test용 select
    @Select("select * from user_profile where user_profile_id = '1'")
    UserDto modalTest();

    @Select("SELECT * FROM posts WHERE board_id = '7' ORDER BY created_at DESC")
    List<PostVo> getNotiList();

    @Select("SELECT p.post_id, u.nickname, p.post_title, b.board_name, p.created_at FROM reports r JOIN posts p ON r.post_id = p.post_id JOIN user_profile u ON r.target_id = u.user_id JOIN boards b ON p.board_id = b.board_id")
    List<PostVo> getRpostsList();

    List<PostVo> getRcommentList();
}