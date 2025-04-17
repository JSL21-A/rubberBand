package com.TTT.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
//    	.formLogin(login -> login
//    			.loginPage("/user/login")
//    			.defaultSuccessUrl("/")
//    			.permitAll()
//    			)
    	.logout(logout -> logout
    			.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
    			.logoutSuccessUrl("/")
    			.permitAll()
    			)
    	.authorizeHttpRequests(auth -> auth
    			.requestMatchers("/").permitAll()
    			);
		return http.build();
		//.permitAll() : 누구나 접근 가능
		//.authenticated() : 인증된 사람만 접근 가능
	}
}
