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

    public List<PostVo> getPostList(int board_id) {
        return publicMapper.getPostList(board_id);
    }

    public List<PostVo> getPostListAll() {
        return publicMapper.getPostListAll();
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

    public void postReport(String user_id, String target_id, Long target) {
        publicMapper.postReport(user_id, target_id, target);
    }

    public String getUserIdBycommentId(Long target) {
        return publicMapper.getUserIdByCommentId(target);
    }

    public void commentReport(String user_id, String target_id, Long target) {
        publicMapper.commentReport(user_id, target_id, target);
    }
}
