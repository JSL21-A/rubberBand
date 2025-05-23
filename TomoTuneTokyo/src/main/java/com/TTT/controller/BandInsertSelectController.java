package com.TTT.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TTT.domain.BandInsertVo;
import com.TTT.service.BandInsertSelectService;

//밴드 결성 후 밴드 상세 조회
@Controller
@RequestMapping("/bandinsertselect")
public class BandInsertSelectController {

	@Autowired
	private BandInsertSelectService bandinsertselectservice;

    @GetMapping("/modify")
    public String showBandDetail(@RequestParam("band_id") String bandId, Model model) {
    	
        // 1. 밴드 기본 정보
        BandInsertVo band = bandinsertselectservice.getBandDetail(bandId);
        model.addAttribute("band", band);

        // 2. 멤버 정보
        List<BandInsertVo> members = bandinsertselectservice.getBandMembers(bandId);
        model.addAttribute("members", members);

        // 3. 태그 정보
        List<BandInsertVo> tags = bandinsertselectservice.getBandTags(bandId);
        model.addAttribute("tags", tags);
        
        // 리더 이메일
        String leaderEmail = bandinsertselectservice.getLeaderEmail(bandId);
        model.addAttribute("leaderEmail", leaderEmail);
        
        // 리더 정보 조회
        BandInsertVo leaderInfo = bandinsertselectservice.selectLeaderInfo(bandId);
        model.addAttribute("leader", leaderInfo);

       
        return "/band/modify"; 
    }
}
