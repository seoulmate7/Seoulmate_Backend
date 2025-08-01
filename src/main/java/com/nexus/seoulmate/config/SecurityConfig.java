package com.nexus.seoulmate.config;

import com.nexus.seoulmate.member.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService){
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((csrf) -> csrf.disable());

        http
                .formLogin((login) -> login.disable());

        http
                .httpBasic((basic) -> basic.disable());

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) ->
                                userInfoEndpointConfig.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/signup/profile-info", true)); // 로그인 성공 후 메인 페이지로 리다이렉트

        http
                .authorizeHttpRequests((auth) -> auth
                        // 인증 없이 접근 가능
                        .requestMatchers("/", "/oauth2/**", "/login/**", "/signup/**").permitAll()
                        // 그 외는 전부 로그인 필요
                        .anyRequest().authenticated());

        return http.build();
    }
}
