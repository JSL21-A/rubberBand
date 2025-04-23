package com.TTT.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

	@Select("select count(*) from users where username = #{username}")
	int checkUsernameExists(@Param("username") String username);
	
}
