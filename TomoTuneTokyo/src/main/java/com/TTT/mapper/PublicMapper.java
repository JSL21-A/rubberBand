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
    @Select("SELECT p.post_id, p.post_title, p.board_id, p.user_id, p.post_pinned, p.created_at, u.nickname FROM posts p, user_profile u WHERE board_id = #{board_id} AND p.user_id = u.user_id ORDER BY p.created_at desc")
    List<PostVo> getPostList(int board_id);

    // 모든 글 불러오기
    @Select("SELECT p.post_id, p.post_title, p.post_like, p.progress, p.board_id, p.user_id, p.post_pinned, p.created_at, u.nickname, (SELECT COUNT(*) FROM comments c, posts p WHERE p.post_id = c.post_id) comments FROM posts p, user_profile u WHERE NOT board_id = 7 AND p.user_id = u.user_id ORDER BY p.created_at desc")
    List<PostVo> getPostListAll();

    // 가장 최근 공지글 3개 불러오기
    @Select("SELECT post_id, post_title, created_at FROM posts WHERE board_id = 7 ORDER BY created_at desc LIMIT 3")
    List<PostVo> getNotiRecently();

    // post_id로 글 내용 가져오기
    @Select("SELECT p.post_id, p.board_id, p.post_title, p.user_id, p.post_content, p.progress, p.post_like, p.created_at, u.nickname FROM posts p, user_profile u WHERE p.post_id = #{post_id} AND p.user_id = u.user_id")
    PostVo getPostView(int post_id);

    @Select("SELECT c.comment_id ,c.user_id, c.comment_content, c.comment_created_at, (SELECT COUNT(*) FROM comments WHERE post_id = #{post_id}) AS comment_cnt, u.nickname FROM comments c, user_profile u WHERE post_id = #{post_id} AND c.user_id = u.user_id")
    List<PostVo> getComment(int post_id);

    @Insert("INSERT INTO comments(post_id, user_id, comment_content) VALUES(#{post_id}, #{user_id}, #{comment_content}) ")
    void insertComment(PostVo vo);

    @Select("SELECT user_id FROM posts WHERE post_id = #{post_id}")
    String getUserIdByPostId(Long post_id);

    @Select("SELECT user_id FROM comments WHERE comment_id = #{comment_id}")
    String getUserIdByCommentId(Long comment_id);

    @Insert("INSERT INTO reports(user_id, target_id, post_id) VALUES(#{user_id}, #{target_id}, #{post_id})")
    void postReport(@Param("user_id") String user_id,
            @Param("target_id") String target_id,
            @Param("post_id") Long post_id);

    @Insert("INSERT INTO reports(user_id, target_id, comment_id) VALUES(#{user_id}, #{target_id}, #{comment_id})")
    void commentReport(@Param("user_id") String user_id,
            @Param("target_id") String target_id,
            @Param("comment_id") Long commnet_id);

}