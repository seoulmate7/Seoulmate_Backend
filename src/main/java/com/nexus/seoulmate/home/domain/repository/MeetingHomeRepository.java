package com.nexus.seoulmate.home.domain.repository;

import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.domain.enums.University;

import java.util.List;

public interface MeetingHomeRepository {

    // 제목에 학교명이 포함된 official 모임 1개
    Meeting findOfficialByTitleContainsUniversity(University univ);

    // 멤버의 hobbies와 동일한 카테고리 모임 5개
    List<Meeting> findRecommendedForUser(Long userId, int limit);

    // 전체 모임을 카테고리별로 집계
    List<CategoryCountRow> countAllGroupByCategory();

    // 카테고리 리스트
    List<Meeting> findByCategory(HobbyCategory category, int page, int size);

    // 전체 모임 중 한국어 클래스 (official, 제목에 한국어 포함)
    List<Meeting> findKoreanClasses(int limit);

    // 카테고리 집계
    record CategoryCountRow(HobbyCategory category, Long count) {}
}
