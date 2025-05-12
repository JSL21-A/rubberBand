package com.TTT.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.UserDto;

@Mapper
public interface AdminMapper {

    // test용 select
    @Select("select * from user_profile where user_profile_id = '1'")
    UserDto modalTest();
}