package com.TTT.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TTT.service.MailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MailTestController {

    private final MailService mailService;

    @GetMapping("/send-code")
    public String sendCode(@RequestParam("email") String email) {
        String code = mailService.sendEmail(email);
        return "認証コード:" + code;
    }
}