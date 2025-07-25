package com.nexus.seoulmate.domain.member.domain;

import com.nexus.seoulmate.domain.member.domain.enums.HobbyCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Hobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOBBY_ID")
    private Long hobbyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HobbyCategory hobbyCategory;

    @Column(nullable = false)
    private String hobbyName;

    @Builder
    public Hobby(String hobbyName, HobbyCategory hobbyCategory){
        this.hobbyName = hobbyName;
        this.hobbyCategory = hobbyCategory;

    }
}
