package com.nexus.seoulmate.domain.member.domain;

import jakarta.persistence.*;

@Entity
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LANGUAGE_ID")
    private Long languageId;

    @Column(nullable = false)
    private String language;
}
