package com.nexus.seoulmate.domain.member.domain;

import jakarta.persistence.*;

@Entity
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UNIV_ID")
    private Long univId;

    @Column(nullable = false)
    private String univName;
}
