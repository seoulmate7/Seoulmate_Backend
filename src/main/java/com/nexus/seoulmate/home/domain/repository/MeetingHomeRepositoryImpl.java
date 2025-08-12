package com.nexus.seoulmate.home.domain.repository;

import com.nexus.seoulmate.meeting.domain.Meeting;
import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import com.nexus.seoulmate.member.domain.enums.University;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MeetingHomeRepositoryImpl implements MeetingHomeRepository{

    private final EntityManager em;

    @Override
    public Meeting findOfficialByTitleContainsUniversity(University univ) {
        String kw = univ.name().replace('_', ' ');

        String jpql = """
            SELECT m FROM Meeting m
            WHERE m.meetingType = :type
              AND LOWER(m.title) LIKE LOWER(CONCAT('%', :kw, '%'))
            ORDER BY m.meetingDay ASC, m.startTime ASC, m.created DESC
            """;
        var list = em.createQuery(jpql, Meeting.class)
                .setParameter("type", MeetingType.OFFICIAL)
                .setParameter("kw", kw)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Meeting> findRecommendedForUser(Long userId, int limit) {
        String jpql = """
            SELECT DISTINCT m
            FROM Meeting m
            WHERE m.hobbyCategory IN (
                SELECT h.hobbyCategory
                FROM Member mb
                JOIN mb.hobbies h
                WHERE mb.userId = :userId
            )
            ORDER BY m.meetingDay ASC, m.created DESC
            """;
        return em.createQuery(jpql, Meeting.class)
                .setParameter("userId", userId)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<CategoryCountRow> countAllGroupByCategory(){
        List<Object[]> rows = em.createQuery("""
        SELECT m.hobbyCategory, COUNT(m)
        FROM Meeting m
        WHERE m.hobbyCategory IS NOT NULL
        GROUP BY m.hobbyCategory
        """, Object[].class).getResultList();

        Map<HobbyCategory, Long> countMap = rows.stream()
                .collect(Collectors.toMap(
                        r -> (HobbyCategory) r[0],
                        r -> (Long) r[1]
                ));

        return Arrays.stream(HobbyCategory.values())
                .map(cat -> new CategoryCountRow(cat, countMap.getOrDefault(cat, 0L)))
                .toList();
    }

    @Override
    public List<Meeting> findByCategory(HobbyCategory category, int page, int size){
        String jpql = """
        SELECT m FROM Meeting m
        WHERE m.hobbyCategory = :cat
        ORDER BY m.meetingDay ASC, m.created DESC
        """;
        return em.createQuery(jpql, Meeting.class)
                .setParameter("cat", category)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Meeting> findKoreanClasses(int limit) {
        String jpql = """
            SELECT m FROM Meeting m
            WHERE m.meetingType = :type
              AND LOWER(m.title) LIKE LOWER('%한국어%')
            ORDER BY m.meetingDay ASC, m.created DESC
            """;
        return em.createQuery(jpql, Meeting.class)
                .setParameter("type", MeetingType.OFFICIAL)
                .setMaxResults(limit)
                .getResultList();
    }
}
