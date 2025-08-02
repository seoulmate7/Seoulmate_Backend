package com.nexus.seoulmate.member.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/status")
    public Map<String, Object> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof OAuth2User) {
            
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            
            response.put("isAuthenticated", true);
            response.put("email", oAuth2User.getAttribute("email"));
            response.put("name", oAuth2User.getAttribute("name"));
            response.put("givenName", oAuth2User.getAttribute("given_name"));
            response.put("familyName", oAuth2User.getAttribute("family_name"));
            response.put("picture", oAuth2User.getAttribute("picture"));
            
        } else {
            response.put("isAuthenticated", false);
            response.put("message", "로그인이 필요합니다.");
        }
        
        return response;
    }
} 