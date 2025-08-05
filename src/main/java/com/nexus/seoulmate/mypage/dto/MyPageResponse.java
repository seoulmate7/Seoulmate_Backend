package com.nexus.seoulmate.mypage.dto;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.enums.University;

import java.util.List;
import java.util.Map;

public class MyPageResponse {
    private String name;
    private String email;
    private String bio;
    private List<Hobby> hobbies;
    private University university;
    private int age;
    private Map<String, Integer> languages;
}
