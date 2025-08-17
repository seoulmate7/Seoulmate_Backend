package com.nexus.seoulmate.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
public class CheckAlbController {

    @GetMapping("/check-alb")
    public String checkAlb(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Headers:\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name).append(": ").append(request.getHeader(name)).append("\n");
        }
        return sb.toString();
    }
}
