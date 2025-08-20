//package com.nexus.seoulmate.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:8080", "http://localhost:5173", "http://3.26.3.167:8080", "https://seoulmate7.shop", "https://seoulmate-frontend.vercel.app")
//                        .allowedMethods("*")
//                        .allowCredentials(true) // 쿠키 포함 허용
//                        .allowedHeaders("*");
//            }
//        };
//    }
//}
