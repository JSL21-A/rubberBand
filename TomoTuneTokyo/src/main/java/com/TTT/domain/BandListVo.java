package com.TTT.domain;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.sql.Timestamp;

public class BandListVo {

    private String postId;               // 게시물 ID
    private String boardId = "R";        // 게시판 ID - 'R' 고정
    private String userId;               // 작성자 ID
    private String postTitle;            // 모집 제목
    private String postContent;          // 모집 글 내용 (모든 세부 정보 포함)
    private MultipartFile postImage;     // 대표 이미지
    private List<MultipartFile> postImages; // 다중 이미지 (DB 저장은 안 함)

    private Timestamp expiryDate; // 마감일
    private String bandId; // 밴드 ID (밴드 이름 참조)
    
    // DB에는 저장하지 않고 postContent에 포함
    private String recruitPosition;     // 모집 포지션 
    private String activityArea;        // 활동 지역
    private String recruitCondition;    // 모집 조건
    private String preferredGenres;     // 장르
    private String leaderComment;       // 리더 코멘트

    // (Optional) tags 필드도 추가 가능
    private String tags;
    
    
    
	public String getBandId() {
		return bandId;
	}

	public void setBandId(String bandId) {
		this.bandId = bandId;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public MultipartFile getPostImage() {
		return postImage;
	}

	public void setPostImage(MultipartFile postImage) {
		this.postImage = postImage;
	}

	public List<MultipartFile> getPostImages() {
		return postImages;
	}

	public void setPostImages(List<MultipartFile> postImages) {
		this.postImages = postImages;
	}

	public Timestamp getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getRecruitPosition() {
		return recruitPosition;
	}

	public void setRecruitPosition(String recruitPosition) {
		this.recruitPosition = recruitPosition;
	}

	public String getActivityArea() {
		return activityArea;
	}

	public void setActivityArea(String activityArea) {
		this.activityArea = activityArea;
	}

	public String getRecruitCondition() {
		return recruitCondition;
	}

	public void setRecruitCondition(String recruitCondition) {
		this.recruitCondition = recruitCondition;
	}

	public String getPreferredGenres() {
		return preferredGenres;
	}

	public void setPreferredGenres(String preferredGenres) {
		this.preferredGenres = preferredGenres;
	}

	public String getLeaderComment() {
		return leaderComment;
	}

	public void setLeaderComment(String leaderComment) {
		this.leaderComment = leaderComment;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
    
    
    

}
