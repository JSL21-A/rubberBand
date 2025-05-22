package com.TTT.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TTT.domain.MypageDto;
import com.TTT.domain.UserDto;
import com.TTT.service.MypageService;
import com.TTT.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;

    @Autowired
    private UserService userService;

    public MypageController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

    // 이력서 입력 폼 열기
    @GetMapping("/resumeInsert")
    public String resumeInsertForm() {
        return "mypage/resumeInsert";
    }

    // 이력서 작성
    @PostMapping("/insert")
    public String insertResume(@ModelAttribute MypageDto dto, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        UserDto user = userService.findByUsername(username);
        String userId = user.getUser_id();

        if (mypageService.hasResume(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "すでに履歴書があります。");
            return "redirect:/mypage/account";
        }

        dto.setUserId(userId);
        mypageService.saveResume(dto);

        return "redirect:/mypage/resumeView";
    }

    // 이력서 조회
    @GetMapping("/resumeView")
    public String viewResume(Model model, Principal principal) {
        String username = principal.getName();
        UserDto user = userService.findByUsername(username);
        String userId = user.getUser_id();

        MypageDto resume = mypageService.getResumeByUserId(userId);

        // 문자열 분리해서 리스트로 전달
        model.addAttribute("resume", resume);
        model.addAttribute("areaList", toList(resume.getArea()));
        model.addAttribute("instrumentList", toList(resume.getInstrument()));
        model.addAttribute("genreList", toList(resume.getGenre()));
        model.addAttribute("practiceDayList", toList(resume.getPracticeDate()));

        return "mypage/resumeView";
    }

    private List<String> toList(String input) {
        return (input != null && !input.isBlank()) ?
            Arrays.asList(input.split(",")) : Collections.emptyList();
    }

    // 프로필 및 계정관리 페이지
    // 등록된 이력서가 있을 경우 : 이력서 보기 / 없을 경우 : 이력서 작성으로 표기되게  
    @GetMapping("/account")
    public String accountSetting(Model model, Principal principal, @ModelAttribute("errorMessage") String errorMessage) {
        String username = principal.getName();
        UserDto user = userService.findByUsername(username);
        boolean hasResume = mypageService.hasResume(user.getUser_id());
        
        model.addAttribute("hasResume", hasResume);
        model.addAttribute("errorMessage", errorMessage);
        return "mypage/accountSetting";
    }
}
