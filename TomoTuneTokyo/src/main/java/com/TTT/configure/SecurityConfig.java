package com.TTT.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        // return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 테스트를 위한 코드
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
          )
          .authorizeHttpRequests(auth -> auth
            // 여기에 인증 없이 접근 가능한 URL들만 나열
            .requestMatchers(
              "/", 
              "/css/**", "/JS/**", "/images/**",
              "/user/check-username",
              "/user/check-nickname",
              "/user/send-email-code",
              "/user/verify-email-code",
              "/user/**"
            ).permitAll()
            .anyRequest().permitAll()
          )
          .formLogin(login -> login
            // 1) 로그인 폼을 렌더링할 GET URL
            .loginPage("/")               
            // 2) 실제 인증 로직을 처리할 POST URL
            .loginProcessingUrl("/user/login")
            .defaultSuccessUrl("/")        
            .permitAll()
          )
          .logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET"))
            .logoutSuccessUrl("/")
            .permitAll()
          );
        return http.build();
    }
}