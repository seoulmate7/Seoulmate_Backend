package com.nexus.seoulmate.domain.member.domain;

import jakarta.persistence.*;

@Entity
public class MemberLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_LANGUAGE_ID")
    private Long memberLanguageId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private Member userId;

    @ManyToOne
    @JoinColumn(name = "LANGUAGE_ID", nullable = false)
    private Language languageId;

    @Column(nullable = false)
    private String languageLevel;
}
