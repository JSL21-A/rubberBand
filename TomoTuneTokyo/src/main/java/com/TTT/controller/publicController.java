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
@RequestMapping({ "/user", "/admin" })
public class publicController {

    @Autowired
    AdminService adminService;

    @GetMapping("/write")
    public String writeNotice(HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin")) {
            if ("true".equals(request.getHeader("HX-Request"))) {
                String prevUri = request.getHeader("referer")
                        .substring(request.getHeader("referer").lastIndexOf("/") + 1);
                model.addAttribute("prev", prevUri);
                return "public/write";
            } else {
                return "redirect:/admin/main";
            }
        }
        return "public/write";
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
