package com.nexus.seoulmate.meeting.domain;

import com.nexus.seoulmate.member.domain.Member;
import com.nexus.seoulmate.member.domain.enums.Languages;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Meeting {

    @Id
    @Column(name = "meeting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "host_message")
    private String hostMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member userId;

    private String category;

    @Column(name = "meeting_day")
    private LocalDate meetingDay;

    @Column(name = "start_time")
    private LocalTime startTime;

    private String location;

    private int price;

    @Column(name = "min_participants")
    private int minParticipants;

    @Column(name = "max_participants")
    private int maxParticipants;

    @Column(name = "current_participants")
    private int currentParticipants;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type")
    private MeetingType meetingType;

    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true) // official 에서 언어 제한 없음
    private Languages language;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @Column(name = "language_level")
    private Integer languageLevel;

    // private 모임 update 메서드
    public void updatePrivateMeeting(String title, String image, String location, String category,
                                     LocalDate meetingDay, LocalTime startTime, int minParticipants,
                                     int maxParticipants, Languages language, String hostMessage, int price){
        this.title = title;
        this.image = image;
        this.location = location;
        this.category = category;
        this.meetingDay = meetingDay;
        this.startTime = startTime;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.language = language;
        this.hostMessage = hostMessage;
        this.price = price;
    }

    // official 모임 update 메서드
    public void updateOfficialMeeting(String title, String image, String location, String category,
                                      LocalDate meetingDay, LocalTime startTime,
                                      int maxParticipants, String hostMessage, int price){
        this.title = title;
        this.image = image;
        this.location = location;
        this.category = category;
        this.meetingDay = meetingDay;
        this.startTime = startTime;
        this.maxParticipants = maxParticipants;
        this.hostMessage = hostMessage;
        this.price = price;
    }
}
