package com.nexus.seoulmate.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class GoogleInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GOOGLE_INFO_ID")
    private Long googleInfoId;

    private String sessionId;

    @Column(nullable = false)
    private String googleId;

    public void updateSessionId(String sessionId){
        this.sessionId = sessionId;
    }

    public GoogleInfo(String sessionId, String googleId) {
        this.sessionId = sessionId;
        this.googleId = googleId;
    }
}
