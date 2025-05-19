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
        System.out.println(postVo.getBoard_id());
        if (postVo.getBoard_id() == 7) {
            publicMapper.insertNotify(postVo);
        } else {
            System.out.println("not today biach");
        }
    }

    public String searchUserByUserName(String username) {
        return publicMapper.searchUserByUserName(username);
    }

    public List<PostVo> getPostList(int board_id) {
        return publicMapper.getPostList(board_id);
    }
}
