package com.TTT.domain;

import java.sql.Timestamp; 

import lombok.Data;

@Data
//댓글
public class MyActiveDto {
    private Long commentId;
    private Long postId;
    private String userId;
    private String commentContent;
    private Timestamp commentCreatedAt;
}
