package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.PostVo;

@Mapper
public interface PublicMapper {

    // posts에 공지 등록
    @Insert("INSERT INTO posts (board_id, user_id, post_title, post_content, post_img, post_pinned) "
            + "VALUES (#{board_id}, #{user_id}, #{post_title}, #{post_content}, #{post_img}, #{post_pinned})")
    void insertNotify(PostVo postVo);

    @Select("SELECT user_id FROM users WHERE username = #{username}")
    String searchUserByUserName(String username);

    @Select("SELECT post_title, board_id, created_at FROM posts WHERE board_id = #{board_id}")
    List<PostVo> getPostList(int board_id);
}