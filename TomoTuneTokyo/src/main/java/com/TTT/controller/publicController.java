package com.TTT.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.TTT.domain.UserDto;
import com.TTT.service.AdminService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping({ "/user", "/admin" })
public class publicController {

    @Autowired
    AdminService adminService;

    @GetMapping("/write")
    public String writePost(HttpServletRequest request, Model model) {
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

    @PostMapping("/doWrite")
    public ResponseEntity<?> doPost(@RequestParam("content") String content,
            @RequestParam(name = "images", required = false) List<MultipartFile> images) {
        System.out.println(images);
        return ResponseEntity.ok().build();
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
