package com.nexus.seoulmate.meeting.domain.repository;

import com.nexus.seoulmate.meeting.api.dto.request.MeetingSearchReq;
import com.nexus.seoulmate.meeting.domain.Meeting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MeetingCustomRepositoryImpl implements MeetingCustomRepository{

    private final EntityManager entityManager;

    @Override
    public List<Meeting> findBySearchCondition(MeetingSearchReq req){
        String jpql = "SELECT m FROM Meeting m WHERE 1=1";

        // 조건이 있으면 문자열 붙임
        StringBuilder queryBuilder = new StringBuilder(jpql);
        Map<String, Object> params = new HashMap<>();

        // 카테고리
        if(req.getHobbyCategory() != null){
            queryBuilder.append(" AND m.category = :category");
            params.put("category", req.getHobbyCategory());
        }

        // 키워드 (제목, 호스트 메시지)
        if(req.getKeyword() != null && !req.getKeyword().isBlank()){
            queryBuilder.append(" AND (m.title LIKE :keyword OR m.hostMessage LIKE :keyword)");
        }

        // 언어 필터 (private만 적용)
        if(req.getLanguage() != null && !req.getLanguage().isBlank()){
            queryBuilder.append(" AND (m.meetingType = 'OFFICIAL' OR m.language = :language");
            params.put("language", req.getLanguage());
        }

        // 언어 레벨 필터 (private만 적용)
        if(req.getLanguageLevel() != null){
            queryBuilder.append(" AND (m.meetingType = 'OFFICIAL' OR (m.languageLevel BETWEEN :minLevel AND :maxLevel))");
            int level = req.getLanguageLevel();
            params.put("minLevel", Math.max(level - 10, 0));
            params.put("maxLevel", Math.min(level + 10, 100));
        }

        TypedQuery<Meeting> query = entityManager.createQuery(queryBuilder.toString(), Meeting.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }
}
