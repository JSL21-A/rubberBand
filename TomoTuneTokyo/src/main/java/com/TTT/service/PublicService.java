package com.TTT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TTT.domain.PostVo;
import com.TTT.mapper.PublicMapper;

@Service
public class PublicService {

    @Autowired
    private PublicMapper publicMapper;

    public void insertPost(PostVo postVo) {

    }
}
