package com.TTT.configure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.TTT.domain.UserDto;
import com.TTT.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Configuration

public class SecurityConfig {
	

  @Bean
  public static PasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();
    DelegatingPasswordEncoder delegating = (DelegatingPasswordEncoder) PasswordEncoderFactories
        .createDelegatingPasswordEncoder();
    delegating.setDefaultPasswordEncoderForMatches(
        new BCryptPasswordEncoder());
    return delegating;
  }
  
  @Autowired
  private UserService userService;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .authorizeHttpRequests(auth -> auth
            // 어드민 페이지를 위한 권한설정.
            .requestMatchers("/admin/**").hasRole("A")
            // 여기에 인증 없이 접근 가능한 URL들만 나열
            .requestMatchers(

              "/", 
              "/css/**", "/JS/**", "/images/**", 
              "/resume/**","/mypage/**",
              "/user/check-username",
              "/user/check-nickname",
              "/user/send-email-code",
              "/user/verify-email-code",
              "/user/register",
              "/user/reset-password",
              "/user/check-username-reset",
              "/user/check-username-email",
              "/user/header-fragment",
              "/test/notify-logout"
            ).permitAll()
            .anyRequest().authenticated()
          )
          .formLogin(login -> login

            // 1) 로그인 폼을 렌더링할 GET URL
            .loginPage("/?openLogin")
            // 2) 실제 인증 로직을 처리할 POST URL
            .loginProcessingUrl("/user/login")
            .successHandler((req, res, auth) -> {
            	String username = auth.getName();
            	UserDto user = userService.findByUsername(username);
            	String userId = user.getUser_id();
              res.setStatus(HttpServletResponse.SC_OK);
              res.setContentType(MediaType.APPLICATION_JSON_VALUE);
              new ObjectMapper().writeValue(res.getWriter(), 
            		  Map.of("userId", userId)
            		  );
            })
            .failureHandler((req, res, ex) -> {
              res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            })
            .permitAll())
        .logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
            .logoutSuccessUrl("/")
            .permitAll());
    return http.build();
  }
}