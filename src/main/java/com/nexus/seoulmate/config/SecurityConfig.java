package com.nexus.seoulmate.config;

import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;
import com.nexus.seoulmate.member.dto.CustomOAuth2User;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberRepository memberRepository;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, MemberRepository memberRepository) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.memberRepository = memberRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(customAuthenticationSuccessHandler()));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/static/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/**", "/signup/**", "/auth/status", "/auth/logout").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println("=== OAuth2 로그인 성공 핸들러 ===");
            System.out.println("Authentication Principal 타입: " + (authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null"));
            System.out.println("Authentication Principal: " + authentication.getPrincipal());

            String email = null;
            if (authentication.getPrincipal() instanceof CustomOAuth2User customOAuth2User) {
                email = customOAuth2User.getOAuth2Response().getEmail();
            } else if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
                email = oAuth2User.getAttribute("email");
            }

            if (email != null) {
                Optional<Member> member = memberRepository.findByEmail(email);

                String redirectUrl;
                if (member.isPresent()) {
                    if (VerificationStatus.SUBMITTED.equals(member.get().getUnivVerification())) {
                        redirectUrl = "/signup/in-progress";
                    } else {
                        redirectUrl = "/home";
                    }
                } else {
                    redirectUrl = "/signup/profile-info";
                }

                // `redirect` 대신 `forward`를 사용하여 서버 내부에서 요청을 전달
                System.out.println("Forwarding to URL: " + redirectUrl);
                request.getRequestDispatcher(redirectUrl).forward(request, response);

            } else {
                response.sendRedirect("/login-failure");
            }
        };
    }
}