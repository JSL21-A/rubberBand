package com.TTT.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SendbirdClient {
    @Value("${sendbird.app-id}") 
    private String appId;

    @Value("${sendbird.api-token}") 
    private String apiToken;


    private final RestTemplate rt = new RestTemplate();

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.set("Api-Token", apiToken);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    // 기본 API 엔드포인트 조합 메서드
    private String baseUrl() {
        return "https://api-" + appId + ".sendbird.com/v3";
    }

    /** Sendbird 유저 생성 */
    public void createUser(String user_id, String nickname) {
        String url = baseUrl() + "/users";
        Map<String, Object> body = Map.of(
            "user_id",  user_id,
            "nickname", nickname,
            "profile_url", ""
        );
        HttpEntity<?> req = new HttpEntity<>(body, headers());
        rt.exchange(url, HttpMethod.POST, req, String.class);
    }

    /** 1:1 채널 생성하고 channel_url 반환 */
    public String createOneOnOneChannel(String u1, String u2) {
        String url = baseUrl() + "/group_channels";
        Map<String, Object> body = Map.of(
            "user_ids",   List.of(u1, u2),
            "is_distinct", true
        );
        HttpEntity<?> req = new HttpEntity<>(body, headers());
        String resp = rt.postForObject(url, req, String.class);

        try {
            JsonNode root = new ObjectMapper().readTree(resp);
            return root.path("channel_url").asText("");
        } catch (IOException e) {
            throw new IllegalStateException("Sendbird 응답 파싱 실패", e);
        }
    }
}