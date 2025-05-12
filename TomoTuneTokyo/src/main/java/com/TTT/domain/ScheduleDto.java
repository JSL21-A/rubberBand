package com.TTT.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ScheduleDto {
	private String date;
	private String title;
	private String color;
}
