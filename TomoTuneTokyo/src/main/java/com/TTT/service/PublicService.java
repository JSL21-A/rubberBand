package com.TTT.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.domain.PostVo;
import com.TTT.domain.UserDto;
import com.TTT.mapper.PublicMapper;

@Service
public class PublicService {

    @Autowired
    private PublicMapper publicMapper;

    public void insertPost(PostVo postVo) {
        if (postVo.getBoard_id() == 7) {
            postVo.setPost_pinned('Y');
        } else {
            postVo.setPost_pinned('N');
        }
        publicMapper.insertPost(postVo);
    }

    public String searchUserByUserName(String username) {
        return publicMapper.searchUserByUserName(username);
    }

    public UserDto getUserIdAndRoleByUsername(String username) {
        return publicMapper.getUserIdAndRoleByUsername(username);
    }

    public List<PostVo> getPostList(int board_id, int showPage) {
        return publicMapper.getPostList(board_id, showPage);
    }

    public List<PostVo> getPostListAll(int showPage) {
        return publicMapper.getPostListAll(showPage);
    }

    public List<PostVo> getNotiRecently() {
        return publicMapper.getNotiRecently();
    }

    public PostVo getPostView(int post_id) {
        return publicMapper.getPostView(post_id);
    }

    public List<PostVo> getComment(int post_id) {
        return publicMapper.getComment(post_id);
    }

    public void insertComment(PostVo vo) {
        publicMapper.insertComment(vo);
    }

    public String getUserIdByPostId(Long target) {
        return publicMapper.getUserIdByPostId(target);
    }

    public String getUserIdByCommentId(Long target) {
        return publicMapper.getUserIdByCommentId(target);
    }

    public void postReport(String user_id, String target_id, Long target) {
        publicMapper.postReport(user_id, target_id, target);
    }

    public String getUserIdBycommentId(Long target) {
        return publicMapper.getUserIdByCommentId(target);
    }

    public void commentReport(String user_id, String target_id, Long target) {
        publicMapper.commentReport(user_id, target_id, target);
    }

    public void editPost(PostVo postVo) {
        publicMapper.editPost(postVo);
    }

    public void deletePost(Long post_id) {
        publicMapper.deletePost(post_id);
    }

    public int getPostCount(int board_id) {
        return publicMapper.getPostCount(board_id);
    }

    public int getPostCountAll() {
        return publicMapper.getPostCountAll();
    }

    public int getNotiCount() {
        return publicMapper.getNotiCount();
    }

    public void deleteComment(Long comment_id) {
        publicMapper.deleteComment(comment_id);
    }
}
