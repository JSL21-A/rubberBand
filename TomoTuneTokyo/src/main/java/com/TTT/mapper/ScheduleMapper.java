package com.TTT.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.TTT.domain.ScheduleDto;

@Mapper
public interface ScheduleMapper {
	@Select("""
			Select
				Date_FORMAT(schedule_start, '%Y-%m-%d') AS date,
				schedule_title AS title,
				schedule_color AS color
			FRROM schedules
			WHERE YEAR(schedule_start) = #{year}
				AND MONTH(schedule_start) = #{month}
				AND deleted_at IS NULL
			ORDER BY schedule_start
			""")
	@Results({
		@Result(column="date", property="date"),
		@Result(column="title", property="title"),
		@Result(column="color", property="color")
	})
	List<ScheduleDto> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
	
	
	
}
