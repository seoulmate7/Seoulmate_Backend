package com.nexus.seoulmate.admin;

import com.nexus.seoulmate.exception.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.nexus.seoulmate.member.domain.Member;

import io.swagger.v3.oas.annotations.tags.Tag;

import static com.nexus.seoulmate.global.status.SuccessStatus.PROCESS_SUCCESS;

@RestController
@RequestMapping("/admin")
@Tag(name = "관리자 페이지", description = "관리자 관련 API")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "학교 인증서 수락 API")
    @PostMapping("/admit/{userId}")
    private Response<Object> admitUnivCertificate(@PathVariable("userId") Long userId){
        Member user = adminService.processCertificate(userId);
        user.admitUnivCertificate();
        return Response.success(PROCESS_SUCCESS, null);
    }

    @Operation(summary = "학교 인증서 거절 API")
    @PostMapping("/reject/{userId}")
    private Response<Object> rejectUnivCertificate(@PathVariable("userId") Long userId){
        Member user = adminService.processCertificate(userId);
        user.rejectUnivCertificate();
        return Response.success(PROCESS_SUCCESS, null);
    }

    // TODO : 관리자 전환 api 
}
