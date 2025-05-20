package com.TTT.controller;

import java.security.Principal;
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

import com.TTT.domain.PostVo;
import com.TTT.domain.UserDto;
import com.TTT.service.AdminService;
import com.TTT.service.PublicService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping({ "/user", "/admin" })
public class publicController {

    @Autowired
    AdminService adminService;

    @Autowired
    PublicService publicService;

    @GetMapping("/list")
    public String postList(HttpServletRequest request, Model model) {
        String Param = request.getParameter("board");
        if (Param == null) {
            List<PostVo> list = publicService.getPostListAll();
            model.addAttribute("list", list);

            List<PostVo> noti = publicService.getPostList(7);
            model.addAttribute("noti", noti);
        } else {
            List<PostVo> list = publicService.getPostList(Integer.parseInt(Param));
            model.addAttribute("list", list);

            if (!(Param.equals("7"))) {
                List<PostVo> noti = publicService.getPostList(7);
                model.addAttribute("noti", noti);
            }
            System.out.println(list);
        }
        return "public/postList";
    }

    @GetMapping("/write")
    public String writePost(HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        if (request.getHeader("referer") != null) {
            String prevUri = request.getHeader("referer")
                    .substring(request.getHeader("referer").lastIndexOf("/") + 1);
            model.addAttribute("prev", prevUri);
        }
        if (isHtmx) {
            System.out.println("onwin");
            model.addAttribute("layout", "layouts/htmxLayout");
            return "public/write";
        } else {
            if (uri.startsWith("/admin")) {
                model.addAttribute("layout", "layouts/adminLayout");
                return "public/write";
            }
            model.addAttribute("layout", "layouts/layout");
            return "public/write";
        }
    }

    @PostMapping("/doWrite")
    public ResponseEntity<?> doPost(@RequestParam("content") String content,
            @RequestParam(name = "images", required = false) List<MultipartFile> images,
            @RequestParam("title") String title, @RequestParam("board_id") Long board_id,
            Principal principal, PostVo vo) {

        vo.setBoard_id(board_id);
        vo.setPost_title(title);
        vo.setPost_content(content);
        vo.setUser_id(publicService.searchUserByUserName(principal.getName()));

        publicService.insertPost(vo);

        return ResponseEntity.ok().build();
    }
}
