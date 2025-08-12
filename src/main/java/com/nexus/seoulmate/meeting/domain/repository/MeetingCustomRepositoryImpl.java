package com.nexus.seoulmate.meeting.domain.repository;

import com.nexus.seoulmate.meeting.api.dto.request.MeetingSearchReq;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
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
        if(req.hobbyCategory() != null){
            queryBuilder.append(" AND m.hobbyCategory = :category");
            params.put("category", req.hobbyCategory());
        }

        // 키워드 (제목, 호스트 메시지)
        if(req.keyword() != null && !req.keyword().isBlank()){
            queryBuilder.append(" AND (LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
                    .append(" OR LOWER(m.hostMessage) LIKE LOWER(CONCAT('%', :keyword, '%')))");
            params.put("keyword", req.keyword().trim());
        }

        // 언어 필터 (private만 적용)
        if(req.language() != null){
            queryBuilder.append(" AND (m.meetingType = :privateType AND m.language = :language)");
            params.put("privateType", MeetingType.PRIVATE);
            params.put("language", req.language());
        }

        // 언어 레벨 필터 (private만 적용)
        Integer minLv = req.minLevelDefault();
        Integer maxLv = req.maxLevelDefault();
        if(req.minLevel() != null || req.maxLevel() != null){
            int min = Math.min(minLv, maxLv);
            int max = Math.max(minLv, maxLv);
            queryBuilder.append(" AND (m.meetingType = :privateType2 AND (m.languageLevel BETWEEN :minLevel AND :maxLevel))");
            params.put("privateType2", MeetingType.PRIVATE);
            params.put("minLevel", min);
            params.put("maxLevel", max);
        }

        // 게시 날짜순 정렬
        queryBuilder.append(" ORDER BY m.created DESC");

        TypedQuery<Meeting> query = entityManager.createQuery(queryBuilder.toString(), Meeting.class);
        params.forEach(query::setParameter);

        // 페이징
        int page = req.pageDefault();
        int size = req.sizeDefault();
        query.setFirstResult(page*size);
        query.setMaxResults(size);

        return query.getResultList();
    }
}
