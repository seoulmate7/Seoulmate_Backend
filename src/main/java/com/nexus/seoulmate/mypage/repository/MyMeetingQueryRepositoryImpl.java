package com.nexus.seoulmate.mypage.repository;

import com.nexus.seoulmate.order.domain.OrderStatus;
import com.nexus.seoulmate.mypage.dto.MeetingSimpleDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class MyMeetingQueryRepositoryImpl implements MyMeetingQueryRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MeetingSimpleDto> findHostedByUserAndMonth(Long userId, LocalDate start, LocalDate end) {
        return em.createQuery(
                        """
                        select new com.nexus.seoulmate.mypage.dto.MeetingSimpleDto(
                                            m.id, m.image, m.title, m.location, m.meetingDay, m.meetingType, m.startTime
                                        )
                                        from Meeting m
                                        where m.userId.userId = :userId
                                          and m.meetingDay between :start and :end
                                        order by m.meetingDay asc, m.startTime asc, m.id asc
                    """,
                        MeetingSimpleDto.class
                )
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    public List<MeetingSimpleDto> findParticipatedConfirmedByUserAndMonth(Long userId, LocalDate start, LocalDate end) {
        return em.createQuery(
                        """
                          select distinct new com.nexus.seoulmate.mypage.dto.MeetingSimpleDto(
                                              m.id, m.image, m.title, m.location, m.meetingDay, m.meetingType, m.startTime
                                          )
                                          from Order o
                                            join o.meeting m
                                            join o.member mem
                                          where mem.userId = :userId
                                            and o.status = :paid
                                            and m.meetingDay between :start and :end
                                          order by m.meetingDay asc, m.startTime asc, m.id asc
            """,
                        MeetingSimpleDto.class
                )
                .setParameter("userId", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("paid", OrderStatus.PAID)
                .getResultList();
    }
}
