package com.nexus.seoulmate.domain.member.domain;

import com.nexus.seoulmate.domain.member.domain.enums.HobbyCategory;
import jakarta.persistence.*;

@Entity
public class Hobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOBBY_ID")
    private Long hobbyId;

    @Column(nullable = false)
    private HobbyCategory hobbyCategory;

    @Column(nullable = false)
    private String hobbyName;
}
