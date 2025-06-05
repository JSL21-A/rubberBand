package com.TTT.domain;

import java.sql.Timestamp; 

import lombok.Data;

@Data
//댓글
public class MyActiveDto {
    private long commentId;
    private long postId;
    private long boardId;
    private String userId;
    private String commentContent;
    private Timestamp commentCreatedAt;
   

}
