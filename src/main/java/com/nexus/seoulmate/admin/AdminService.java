package com.nexus.seoulmate.admin;

import com.nexus.seoulmate.exception.CustomException;
import com.nexus.seoulmate.global.status.ErrorStatus;
import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.VerificationStatus;
import com.nexus.seoulmate.member.repository.MemberRepository;
import com.nexus.seoulmate.member.service.MemberService;
import com.nexus.seoulmate.member.domain.enums.Role;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public Member processCertificate(Long userId){
        Member adminUser = memberService.getCurrentUser();

        Member user = memberRepository.findByUserId(userId);

        // 로그인한 사람이 ADMIN인지 확인
        if (adminUser.getRole() != Role.ADMIN){
            throw new CustomException(ErrorStatus.USER_INVALID_REQUEST);
        }

        // 사용자 상태가 SUBMITTED가 맞는지 확인
        if (user.getUnivVerification() != VerificationStatus.SUBMITTED){
            throw new CustomException(ErrorStatus.STATUS_CONFLICT);
        }

        return user;
    }
}
