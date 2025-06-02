package com.TTT.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.TTT.domain.UserDto;
import com.TTT.service.AdminService;
import com.TTT.service.NotificationService;
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

    @Autowired
    NotificationService notificationService;

    // 글 목록 보여주기
    @GetMapping("/list")
    public String postList(HttpServletRequest request, Model model) {
        String Param = request.getParameter("board");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 0;
        int showPage = (page <= 1) ? 0 : (page - 1) * 10;
        int count = 0;

        List<PostVo> list = null;
        List<PostVo> noti = null;
        // 선택된 카테고리가 으면
        if (Param == null) {
            // 모든 카테고리에서 글 가져오기
            list = publicService.getPostListAll(showPage);
            count = publicService.getPostCountAll();
            // 상단에 배치될 가장 최근 공지사항 3개
            noti = publicService.getNotiRecently();
            // 카테고리가 선택되어 있으면
        } else {
            // 해당 카테고리의 글 가져오기.(Param = board_id)
            list = publicService.getPostList(Integer.parseInt(Param), showPage);
            count = publicService.getPostCount(Integer.parseInt(Param));
            // 만일 선택된 카테고리가 공지사항이라면 모든 공지사항 가져오기
            if (!(Param.equals("7"))) {
                noti = publicService.getNotiRecently();
                count = publicService.getPostCount(Integer.parseInt(Param));
            }
        }

        model.addAttribute("count", count);
        model.addAttribute("list", list);
        model.addAttribute("noti", noti);
        return "public/postList";
    }

    // 글쓰기
    @GetMapping("/write")
    public String writePost(HttpServletRequest request, Model model,
            @RequestParam(name = "edit", required = false) String postId, Principal principal) {
        // 수정이라면 수정할 글 가져오기
        if (postId != null) {
            PostVo post = publicService.getPostView(Integer.parseInt(postId));
            if (post.getUser_id().equals(publicService.searchUserByUserName(principal.getName()))) {
                model.addAttribute("post", post);
            } else {
                return "redirect:/user/error";
            }
        }
        // 현재 uri 가져오기.
        String uri = request.getRequestURI();
        // htmx로 인한 요청인지 체크
        boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        model.addAttribute("banner_title", "public/write :: banner_title");
        // 이전 페이지의 데이터가 있다면.
        if (request.getHeader("referer") != null) {
            // 이전 페이지의 경로 저장. (글쓴곳으로 다시 보내기 위함.)
            String prevUri = request.getHeader("referer")
                    .substring(request.getHeader("referer").lastIndexOf("/") + 1);
            // 만약에 5,10 부분이 board라면
            if (prevUri.length() >= 12 && prevUri.substring(5, 10).equals("board")) {
                // board 파라미터값 저장.
                request.getSession().setAttribute("prev", prevUri.substring(11, 12));
            } else {
                // 아니면 그냥 uri 저장.
                request.getSession().setAttribute("prev", prevUri);
            }
        }
        // htmx 요청이면
        if (isHtmx) {
            // 페이지중 일부만 반환
            return "public/write :: content";
            // 일반 요청이면
        } else {
            if (uri.startsWith("/admin")) {
                // 어드민페이지에서 요청한거면 어드민 레이아웃
                model.addAttribute("layout", "layouts/adminLayout");
                // 전체 페이지 반환
                return "public/write";
            }
            // 어드민 페이지 아니면 메인 레이아웃.
            model.addAttribute("layout", "layouts/layout");
            return "public/write";
        }
    }

    @PostMapping("/doWrite")
    public ResponseEntity<?> doPost(@RequestParam("content") String content,
            @RequestParam(name = "edit", required = false) Long postId,
            @RequestParam(name = "images", required = false) List<MultipartFile> images,
            @RequestParam("title") String title, @RequestParam("board_id") Long board_id,
            Principal principal, PostVo vo) throws IOException {

        // 1. 이미지 저장 (data-id -> 저장된 URL)
        Map<String, String> imageUrlMap = new HashMap<>();
        // 이미지가 있으면
        if (images != null) {
            for (MultipartFile file : images) {
                // 이미지 이름 추출
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

        // 2. HTML 전달 및 <img src> 치환. (src를 base64로 인코딩해서 바로 페이지에 보여주는거 실제 파일 경로로 바꾸기
        // 위함.)
        Document doc = Jsoup.parseBodyFragment(content);
        // 전달된 html에서 img에 data-id가 있는 태그만
        for (Element img : doc.select("img[data-id]")) {
            // 위에서 자장한 uuid로 매핑되어 저장된 경로 가져오기.
            String dataId = img.attr("data-id");
            String imageUrl = imageUrlMap.get(dataId);
            // url을 갖는 이미지가 있으면 src태그 치환.
            if (imageUrl != null) {
                img.attr("src", imageUrl);
                img.removeAttr("data-id");
            }
        }

        // 3. YouTube iframe만 허용 (옵션) (되는지 안해봤음.)
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

        if (postId != null) {
            vo.setPost_id(postId);
            publicService.editPost(vo);
            return ResponseEntity.ok().build();
        }
        publicService.insertPost(vo);

        return ResponseEntity.ok().build();
    }

    // 위에서 거르고 거른 진짜 업로드 되야하는 이미지만 업로드하기위한 함수.
    public String saveImage(MultipartFile file, String uuid) throws IOException {

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }

        // 이미지 이름을 uuid + 확장자로 저장
        String newFileName = uuid + "." + ext;

        // 당장은 프로젝트 폴더에 업로드 배포할땐 서버내의 업로드경로
        Path uploadDir = Paths.get(System.getProperty("user.dir") + "/src/main/resources/static/images/uploads/");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(newFileName);
        file.transferTo(filePath.toFile());

        return "/images/uploads/" + newFileName;
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

    @GetMapping("/view")
    public String viewPost(HttpServletRequest request, Model model, Principal principal) {
        boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        String uri = request.getRequestURI();

        String Param = request.getParameter("post");
        PostVo post = publicService.getPostView(Integer.parseInt(Param));
        List<PostVo> comment = publicService.getComment(Integer.parseInt(Param));

        if (comment == null || comment.size() <= 0) {
            PostVo noComment = new PostVo();
            noComment.setComment_cnt(0);
            model.addAttribute("comment_cnt", 0);
        } else {
            model.addAttribute("comment_cnt", comment.get(0).getComment_cnt());
            model.addAttribute("comment", comment);
        }

        model.addAttribute("post", post);
        model.addAttribute("user", publicService.searchUserByUserName(principal.getName()));

        if (isHtmx) {
            // 페이지중 일부만 반환
            return "public/postView :: content";
            // 일반 요청이면
        } else {
            if (uri.startsWith("/admin")) {
                // 어드민페이지에서 요청한거면 어드민 레이아웃
                model.addAttribute("layout", "layouts/adminLayout");
                // 전체 페이지 반환
                return "public/postView";
            }
            // 어드민 페이지 아니면 메인 레이아웃.
            model.addAttribute("layout", "layouts/layout");
            return "public/postView";
        }
    }

    @PostMapping("/commentWrite")
    public ResponseEntity<Object> commentWrite(@RequestParam("comment") String comment,
            @RequestParam("post_id") Long post_id, @RequestParam("post_user_id") String post_user_id,
            Principal principal, PostVo vo) {

        vo.setComment_content(comment);
        vo.setUser_id(publicService.searchUserByUserName(principal.getName()));
        publicService.insertComment(vo);

        // 알림 전송 (post_user_id는 게시물 작성자)
        notificationService.sendNotification(post_user_id, "comment", "新しいコメントが届きました。", "/user/view?post=" + post_id);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Object> reportPost(@RequestParam("target") Long target, @RequestParam("type") String type,
            Principal principal) {

        if (type.equals("post")) {
            String target_id = publicService.getUserIdByPostId(target);
            String user_id = publicService.searchUserByUserName(principal.getName());
            publicService.postReport(user_id, target_id, target);
        }

        if (type.equals("comment")) {
            String target_id = publicService.getUserIdBycommentId(target);
            String user_id = publicService.searchUserByUserName(principal.getName());
            publicService.commentReport(user_id, target_id, target);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete")
    public ResponseEntity<Object> deletePost(@RequestParam("target") Long target, Principal principal, Model model,
            UserDto dto) {
        String target_user_id = publicService.getUserIdByPostId(target);
        dto = publicService.getUserIdAndRoleByUsername(principal.getName());
        if (dto.getRole().equals("A") || target_user_id.equals(dto.getUser_id())) {
            publicService.deletePost(target);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("bad request");
        }
    }

    @GetMapping("/delComment")
    public ResponseEntity<Object> deleteComment(@RequestParam("target") Long target, Principal principal, Model model,
            UserDto dto) {
        String target_user_id = publicService.getUserIdByCommentId(target);
        dto = publicService.getUserIdAndRoleByUsername(principal.getName());
        if (dto.getRole().equals("A") || target_user_id.equals(dto.getUser_id())) {
            publicService.deleteComment(target);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("bad request");
        }
    }

    @GetMapping("/error")
    public String errorPage(Model model) {
        model.addAttribute("layout", "layouts/layout");
        return "public/error";
    }

}
