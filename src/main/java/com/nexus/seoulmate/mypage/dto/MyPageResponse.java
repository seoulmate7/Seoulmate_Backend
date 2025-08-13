package com.nexus.seoulmate.mypage.dto;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.enums.Languages;
import com.nexus.seoulmate.member.domain.enums.University;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class MyPageResponse {
    private String profileImageUrl;
    private String name;
    private String email;
    private String bio;
    private List<Hobby> hobbies;
    private University university;
    private int age;
    private Map<Languages, Integer> languages;
}
