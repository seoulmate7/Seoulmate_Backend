package com.nexus.seoulmate.config;

import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

import static com.nexus.seoulmate.member.domain.enums.VerificationStatus.SUBMITTED;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberRepository memberRepository;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, MemberRepository memberRepository){
        this.customOAuth2UserService = customOAuth2UserService;
        this.memberRepository = memberRepository;
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
                        .successHandler((request, response, authentication) -> {
                            // OAuth2User에서 이메일 추출
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oAuth2User.getAttribute("email");

                            // 이메일로 회원 조회
                            Optional<Member> member = memberRepository.findByEmail(email);

                            if (member.isPresent()) { // 회원가입된 사용자 (로그인 성공하면)
                                if (member.get().getUnivVerification().equals(SUBMITTED)){
                                    response.sendRedirect("/signup/in-progress");
                                } else {
                                    response.sendRedirect("/home");
                                }
                            } else { // 회원가입되지 않은 사용자는 기존 경로로 리디렉트
                                response.sendRedirect("/signup/profile-info");
                            }
                        }));

        http
                .authorizeHttpRequests((auth) -> auth
                        // 정적 리소스는 인증 없이 접근 가능
                        .requestMatchers("/", "/index.html", "/static/**").permitAll()
                        // Swagger UI 관련 경로들
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        // 인증 관련 경로들
                        .requestMatchers("/oauth2/**", "/login/**", "/signup/**", "/auth/status", "/auth/logout").permitAll()
                        // /seoulmate는 인증 필요
                        .requestMatchers("/home/**").authenticated()

                        .anyRequest().authenticated());

        return http.build();
    }
}
