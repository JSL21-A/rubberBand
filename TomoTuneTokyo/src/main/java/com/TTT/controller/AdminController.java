package com.TTT.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TTT.domain.PostVo;
import com.TTT.domain.UserDto;
import com.TTT.service.AdminService;
import com.TTT.service.PublicService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    PublicService publicService;

    @GetMapping("/")
    public String rediMain() {
        return "redirect:/admin/main";
    }

    @GetMapping("/main")
    public String adminMain() {
        return "admin/adminIndex";
    }

    @GetMapping("/users")
    public String userList(HttpServletRequest request) {
        List<UserDto> user = adminService.getAllUser();
        return "admin/memberList";
    }

    @GetMapping("/notify")
    public String noticeList(HttpServletRequest request, Model model) {
        List<PostVo> list = adminService.getNotiList();
        model.addAttribute("list", list);
        return "admin/noticeList";
    }

    @GetMapping("/QnA")
    public String QnAList(HttpServletRequest request) {
        return "admin/QnA";
    }

    @GetMapping("/reports")
    public String reportList(HttpServletRequest request, Model model) {
        List<PostVo> posts = adminService.getRpostsList();
        List<PostVo> comments = adminService.getRcommentList();
        model.addAttribute("posts", posts);
        model.addAttribute("comment", comments);
        return "admin/report";
    }

}
