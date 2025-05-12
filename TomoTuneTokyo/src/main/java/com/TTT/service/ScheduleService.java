package com.TTT.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.TTT.domain.ScheduleDto;
import com.TTT.mapper.ScheduleMapper;

@Service
public class ScheduleService {

	private final ScheduleMapper mapper;
	
	public ScheduleService(ScheduleMapper mapper) {
		this.mapper = mapper;
	}
	
	//연, 월 스케쥴 날짜별 Map으로 return
	public Map<String, List<ScheduleDto>> getMonthly(int year, int month){
		List<ScheduleDto> list = mapper.findByYearAndMonth(year, month);
		return list.stream()
				.collect(Collectors.groupingBy(ScheduleDto::getDate));
	}
	
}
