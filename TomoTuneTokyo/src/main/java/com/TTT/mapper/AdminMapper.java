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

    @Select("SELECT * FROM posts WHERE board_id = '7'")
    List<PostVo> getNotiList();
}