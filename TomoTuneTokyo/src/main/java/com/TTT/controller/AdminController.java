package com.TTT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/main")
    public String adminMain() {
        return "admin/adminIndex";
    }

    @GetMapping("/list")
    public String userList(HttpServletRequest request, @RequestParam(name = "page", required = false) String page) {
        if ("true".equals(request.getHeader("HX-Request"))) {
            page = (page == null) ? "1" : "3";
            System.out.println(page);
            return "admin/memberList";
        } else {
            return "redirect:/admin/main";
        }
    }

    @GetMapping("/notice")
    public String noticeList(HttpServletRequest request) {
        if ("true".equals(request.getHeader("HX-Request"))) {
            return "admin/noticeList";
        } else {
            return "redirect:/admin/main";
        }
    }
}
