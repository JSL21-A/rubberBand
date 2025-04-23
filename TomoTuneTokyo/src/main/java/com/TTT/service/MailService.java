package com.TTT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    // 인증코드 생성
    public String createCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // 0~9 숫자
        }
        return code.toString();
    }

    // 메일 전송
    public String sendEmail(String toEmail) {
        String code = createCode();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[TomoTuneTokyo] メール認証コードです");
        message.setText("認証コード: " + code + "\n\n５分以内にご入力お願いします!");

        mailSender.send(message);
        return code; // 이 코드를 Redis나 DB에 저장해두면 돼
    }
}