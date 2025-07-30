package com.nexus.seoulmate.member.domain;

import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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
