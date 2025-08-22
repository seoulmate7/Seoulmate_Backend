package com.nexus.seoulmate.meeting.domain.repository;

import com.nexus.seoulmate.meeting.api.dto.request.MeetingSearchReq;
import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.member.domain.enums.Languages;
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
        // 기본 서치
        StringBuilder queryBuilder = new StringBuilder("SELECT m FROM Meeting m WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        // 카테고리
        if(req.hobbyCategory() != null){
            queryBuilder.append("""
                    AND (
                           m.hobbyCategory = :category
                        OR (m.primaryHobby IS NOT NULL AND m.primaryHobby.hobbyCategory = :category)
                    )
                """);
            params.put("category", req.hobbyCategory());
        }

        // 키워드 (제목, 호스트 메시지)
        if(req.keyword() != null && !req.keyword().isBlank()){
            queryBuilder.append(" AND (LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
                    .append(" OR LOWER(m.hostMessage) LIKE LOWER(CONCAT('%', :keyword, '%')))");
            params.put("keyword", req.keyword().trim());
        }

        // 언어 레벨 필터 (private에만)
        queryBuilder.append(" AND (");
        // official은 항상 통과
        queryBuilder.append(" m.meetingType <> :privateType ");
        // private 조건
        queryBuilder.append(" OR (m.meetingType = :privateType ");
        params.put("privateType", MeetingType.PRIVATE);

        if(req.koreanSelected()){
            // 한국어
            queryBuilder.append(" AND m.language = :langKorean ");
            queryBuilder.append(" AND (m.languageLevel IS NULL OR m.languageLevel BETWEEN :koMin AND :koMax) ");
            params.put("langKorean", Languages.KOREAN);
            params.put("koMin", req.koMinDefault());
            params.put("koMax", req.koMaxDefault());
        } else if (req.englishSelected()){
            // 영어
            queryBuilder.append(" AND m.language = :langEnglish ");
            queryBuilder.append(" AND (m.languageLevel IS NULL OR m.languageLevel BETWEEN :enMin AND :enMax) ");
            params.put("langEnglish", Languages.ENGLISH);
            params.put("enMin", req.enMinDefault());
            params.put("enMax", req.enMaxDefault());
        } else{
            // 언어 미선택
            queryBuilder.append("""
                AND (
                       (m.language = :langKorean AND (m.languageLevel IS NULL OR m.languageLevel BETWEEN :koMin AND :koMax))
                    OR (m.language = :langEnglish AND (m.languageLevel IS NULL OR m.languageLevel BETWEEN :enMin AND :enMax))
                )
                """);
            params.put("langKorean", Languages.KOREAN);
            params.put("langEnglish", Languages.ENGLISH);
            params.put("koMin", req.koMinDefault());
            params.put("koMax", req.koMaxDefault());
            params.put("enMin", req.enMinDefault());
            params.put("enMax", req.enMaxDefault());
        }
        queryBuilder.append(") )");

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
