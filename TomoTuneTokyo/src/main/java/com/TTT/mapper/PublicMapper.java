package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.PostVo;

@Mapper
public interface PublicMapper {

    // posts에 글 등록
    @Insert("INSERT INTO posts (board_id, user_id, post_title, post_content, post_img, post_pinned) "
            + "VALUES (#{board_id}, #{user_id}, #{post_title}, #{post_content}, #{post_img}, #{post_pinned})")
    void insertPost(PostVo postVo);

    // 로그인한 username으로 user_id 찾기
    @Select("SELECT user_id FROM users WHERE username = #{username}")
    String searchUserByUserName(String username);

    // 카테고리에 해당하는 글 불러오기
    @Select("SELECT post_title, board_id, user_id, post_pinned, created_at FROM posts WHERE board_id = #{board_id}")
    List<PostVo> getPostList(int board_id);

    // 모든 글 불러오기
    @Select("SELECT p.post_title, p.board_id, p.user_id, p.post_pinned, p.created_at, u.nickname FROM posts p, user_profile u WHERE NOT board_id = 7 AND p.user_id = u.user_id;")
    List<PostVo> getPostListAll();
}