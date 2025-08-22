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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private Member userId;

    private String sessionId;

    @Column(nullable = false)
    private String googleId;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String firstName;

    @Column(length = 20)
    private String lastName;

    public void saveUserId(Member userId){
        this.userId = userId;
    }

    public void updateSessionId(String sessionId){
        this.sessionId = "JSESSIONID=" + sessionId;
    }

    public GoogleInfo(String sessionId, String googleId, String email, String firstName, String lastName) {
        this.sessionId = "JSESSIONID=" + sessionId;
        this.googleId = googleId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
