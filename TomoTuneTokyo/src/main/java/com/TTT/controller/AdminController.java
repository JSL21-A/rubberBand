package com.TTT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TTT.domain.UserDto;
import com.TTT.service.AdminService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/")
    public String rediMain() {
        return "redirect:/admin/main";
    }

    @GetMapping("/main")
    public String adminMain() {
        return "admin/adminIndex";
    }

    @GetMapping("/users")
    public String userList(HttpServletRequest request, @RequestParam(name = "page", required = false) String page) {
        if ("true".equals(request.getHeader("HX-Request"))) {
            page = (page == null) ? "1" : page;
            return "admin/memberList";
        } else {
            return "redirect:/admin/main";
        }
    }

    @GetMapping("/notify")
    public String noticeList(HttpServletRequest request) {
        if ("true".equals(request.getHeader("HX-Request"))) {
            return "admin/noticeList";
        } else {
            return "redirect:/admin/main";
        }
    }

    @GetMapping("/QnA")
    public String QnAList(HttpServletRequest request) {
        if ("true".equals(request.getHeader("HX-Request"))) {
            return "admin/QnA";
        } else {
            return "redirect:/admin/main";
        }
    }

    @GetMapping("/reports")
    public String reportList(HttpServletRequest request) {
        if ("true".equals(request.getHeader("HX-Request"))) {
            return "admin/report";
        } else {
            return "redirect:/admin/main";
        }
    }

    @GetMapping("/test")
    public String modalTest(HttpServletRequest request, UserDto userDto, Model model) {
        if ("true".equals(request.getHeader("HX-Request"))) {
            adminService.modalTest(userDto);
            model.addAttribute("test", userDto.getNickname());
            return "admin/testModal";
        } else {
            return "redirect:/admin/main";
        }
    }
}
