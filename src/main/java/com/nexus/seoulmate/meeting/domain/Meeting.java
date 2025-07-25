package com.nexus.seoulmate.meeting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting {

    @Id
    @Column(name = "meeting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "host_message")
    private String message;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User userId;

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

    private Language language;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @Column(name = "language_level")
    private int languageLevel;
}
