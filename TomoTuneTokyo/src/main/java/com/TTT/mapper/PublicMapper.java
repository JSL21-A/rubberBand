package com.TTT.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.PostVo;

@Mapper
public interface PublicMapper {

    // posts에 글 등록
    @Insert("insert into posts (board_id, user_id, post_title, post_content, post_img, post_pinned, post_status, progress) "
            + "values (#{board_id}, #{user_id}, #{post_title}, #{post_content}, #{post_img}, 'E', 'A')")
    void insertNotify(PostVo postVo);
}