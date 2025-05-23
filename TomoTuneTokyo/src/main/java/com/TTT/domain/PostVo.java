package com.TTT.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostVo {

    private Long post_id; // 글 번호(자동생성)
    private Long board_id; // 카테고리 번호
    private String user_id; // 작성자 번호
    private String nickname; // 작성자 닉네임
    private String post_title; // 글 이름
    private String post_content; // 글 내용
    private String post_img; // 이미지가 있다면 경로 저장
    private char post_pinned; // 공지 등 고정되어야 할 글이면 Y
    private char post_status; // 활성화중인지 아닌지
    private char progress; // 거래중 모집중등의 상태.
    private Integer post_like; // 좋아요 수
    private LocalDateTime created_at; // 생성날짜
    private LocalDateTime updated_at; // 수정날짜
    private LocalDateTime deleted_at; // 삭제날짜

}
