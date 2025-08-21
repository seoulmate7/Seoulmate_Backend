package com.nexus.seoulmate.mypage.repository;

import com.nexus.seoulmate.meeting.domain.MeetingType;
import com.nexus.seoulmate.order.domain.OrderStatus;
import com.nexus.seoulmate.mypage.dto.MeetingSimpleDto;
import com.nexus.seoulmate.payment.domain.PaymentStatus;
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
    public List<MeetingSimpleDto> findHostedByUserAndDate(Long userId, LocalDate meetingDay) {
        return em.createQuery(
                        """
                    
                                select new com.nexus.seoulmate.mypage.dto.MeetingSimpleDto(
                                        m.id, m.image, m.title, m.location, m.meetingDay, m.meetingType, m.startTime
                                    )
                                    from Meeting m
                                    where m.userId.userId = :userId
                                      and m.meetingDay = :meetingDay
                                    order by m.startTime asc, m.id asc
                    """,
                        MeetingSimpleDto.class
                )
                .setParameter("userId", userId)
                .setParameter("meetingDay", meetingDay)
                .getResultList();
    }

    @Override
    public List<MeetingSimpleDto> findParticipatedConfirmedByUserAndDate(Long userId, LocalDate meetingDay) {
        return em.createQuery(
                        """
                                select distinct new com.nexus.seoulmate.mypage.dto.MeetingSimpleDto(
                               m.id, m.image, m.title, m.location, m.meetingDay, m.meetingType, m.startTime
                           )
                           from Order o
                           join o.meeting m
                           join o.member mem
                           where mem.userId = :userId
                             and m.meetingDay = :meetingDay
                             and o.status = :paid
                           order by m.startTime asc, m.id asc
            """,
                        MeetingSimpleDto.class
                )
                .setParameter("userId", userId)
                .setParameter("meetingDay", meetingDay)
                .setParameter("paid", OrderStatus.PAID)
                .getResultList();
    }
}
