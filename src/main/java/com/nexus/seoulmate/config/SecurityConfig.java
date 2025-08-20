package com.nexus.seoulmate.config;

import com.nexus.seoulmate.member.handler.CustomAuthenticationSuccessHandler;
import com.nexus.seoulmate.member.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. CORS 설정을 Security 필터 체인에 추가
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(customAuthenticationSuccessHandler));

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/static/**", "/health-check").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/**", "/signup/**", "/auth/**","/meetings/**").permitAll()
                        .requestMatchers("/home/**").authenticated()
                        .anyRequest().authenticated());

        return http.build();
    }

    // 2. CORS 상세 설정을 위한 Bean 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 Origin 목록
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8080", 
            "http://localhost:5173", 
            "http://3.26.3.167:8080", 
            "https://seoulmate7.shop", 
            "https://seoulmate-frontend.vercel.app"
        ));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of("*"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));
        
        // 쿠키 및 인증 정보 포함 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 위 설정 적용
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}


//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//                 .cors(cors -> cors.configurationSource(corsConfigurationSource()));

//         http
//                 .csrf(csrf -> csrf.disable())
//                 .formLogin(login -> login.disable())
//                 .httpBasic(basic -> basic.disable())
//                 .sessionManagement(session -> session
//                         .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//                 .oauth2Login(oauth2 -> oauth2
//                         .userInfoEndpoint(userInfoEndpointConfig ->
//                                 userInfoEndpointConfig.userService(customOAuth2UserService))
//                         .successHandler(customAuthenticationSuccessHandler));

//         http
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/", "/index.html", "/static/**", "/health-check").permitAll()
//                         .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

//                         // 인증 관련 경로들
//                         .requestMatchers("/oauth2/**", "/login/**", "/signup/**", "/auth/**","/meetings/**").permitAll()
//                         // /seoulmate는 인증 필요
//                         .requestMatchers("/home/**").authenticated()

//                         .anyRequest().authenticated());

//         return http.build();
//     }
