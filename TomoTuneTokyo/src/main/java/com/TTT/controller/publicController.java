package com.TTT.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.TTT.domain.PostVo;
import com.TTT.service.AdminService;
import com.TTT.service.PublicService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("mParam", Param);
        if (Param == null) {
            List<PostVo> list = publicService.getPostListAll();
            model.addAttribute("list", list);

            List<PostVo> noti = publicService.getNotiRecently();
            model.addAttribute("noti", noti);
        } else {
            List<PostVo> list = publicService.getPostList(Integer.parseInt(Param));
            model.addAttribute("list", list);

            if (!(Param.equals("7"))) {
                List<PostVo> noti = publicService.getNotiRecently();
                model.addAttribute("noti", noti);
            }
        }
        return "public/postList";
    }

    @GetMapping("/write")
    public String writePost(HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        model.addAttribute("banner_title", "public/write :: banner_title");
        if (request.getHeader("referer") != null) {
            String prevUri = request.getHeader("referer")
                    .substring(request.getHeader("referer").lastIndexOf("/") + 1);
            if (prevUri.length() >= 12 && prevUri.substring(5, 10).equals("board")) {
                request.getSession().setAttribute("prev", prevUri.substring(11, 12));
            } else {
                request.getSession().setAttribute("prev", prevUri);
            }
        }
        if (isHtmx) {
            return "public/write :: content";
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
            Principal principal, PostVo vo) throws IOException {

        // 1. 이미지 저장 (data-id -> 저장된 URL)
        Map<String, String> imageUrlMap = new HashMap<>();
        if (images != null) {
            for (MultipartFile file : images) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null)
                    continue;

                // UUID 추출
                String uuid = originalFilename.split("\\.")[0];

                // 저장된 URL 반환
                String savedUrl = saveImage(file, uuid); // 예: "/upload/images/uuid.jpg"
                imageUrlMap.put(uuid, savedUrl);
            }
        }

        // 2. HTML 파싱 및 <img src> 치환
        Document doc = Jsoup.parseBodyFragment(content);
        for (Element img : doc.select("img[data-id]")) {
            String dataId = img.attr("data-id");
            String imageUrl = imageUrlMap.get(dataId);
            if (imageUrl != null) {
                img.attr("src", imageUrl);
                img.removeAttr("data-id");
            }
        }

        // 3. YouTube iframe만 허용 (옵션)
        for (Element iframe : doc.select("iframe")) {
            String src = iframe.attr("src");
            if (!src.startsWith("https://www.youtube.com/embed/")) {
                iframe.remove(); // 보안상 삭제
            }
        }

        // 4. 위험 태그/속성 제거
        sanitizeHtml(doc.body());

        // 5. 최종 HTML 치환
        String cleanedHtml = doc.body().html();

        vo.setBoard_id(board_id);
        vo.setPost_title(title);
        vo.setPost_content(cleanedHtml);
        vo.setUser_id(publicService.searchUserByUserName(principal.getName()));

        publicService.insertPost(vo);

        return ResponseEntity.ok().build();
    }

    public String saveImage(MultipartFile file, String uuid) throws IOException {

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }

        String newFileName = uuid + "." + ext;

        Path uploadDir = Paths.get(System.getProperty("user.dir") + "/upload/images");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(newFileName);
        file.transferTo(filePath.toFile());

        return "/upload/images/" + newFileName;
    }

    public void sanitizeHtml(Element body) {
        // 위험 태그 제거
        body.select("script, style, object, embed, link, meta").remove();

        // 위험 속성 제거
        for (Element el : body.select("*")) {
            el.removeAttr("onload");
            el.removeAttr("onclick");
            el.removeAttr("onerror");
            el.removeAttr("style"); // 필요시 제거
        }
    }
}
