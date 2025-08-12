package com.nexus.seoulmate.member.domain;

import com.nexus.seoulmate.member.domain.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String firstName;
    @Column(nullable = false, length = 20)
    private String lastName;

    @Column(nullable = false)
    private LocalDate DOB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Countries country;

    @Column(nullable = false, length = 120)
    private String bio;

    @Column(nullable = false)
    private String profileImage;

    @ElementCollection
    @CollectionTable(name = "MEMBER_LANGUAGE", joinColumns = @JoinColumn(name = "USER_ID"))
    @MapKeyColumn(name = "LANGUAGE")
    @Column(name = "LEVEL")
    @Builder.Default
    private Map<String, Integer> languages = new HashMap<>();
    // 언어 + 언어 레벨

    @ManyToMany
    @Builder.Default
    private List<Hobby> hobbies = new ArrayList<>();

    @Column(nullable = false)
    private String univCertificate; // 학교 인증서

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private University univ; // 학교 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus univVerification; // 학교 인증 됐는지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // user, admin

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider; // 일반 회원가입, google 등

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus; // 탈퇴인지 아닌지

    public static Member createGoogleUser(String email, String firstName, String lastName,
                                        LocalDate DOB, Countries country, String bio, String profileImage, List<Hobby> hobbies,
                                        String univCertificate, University univ, Map<String, Integer> languages,
                                        VerificationStatus verificationStatus, AuthProvider authProvider){
        Member user = new Member();
        user.email = email;
        user.password = "oauth2"; // 의미 없는 값
        user.firstName = firstName;
        user.lastName = lastName;
        user.DOB = DOB;
        user.country = country;
        user.bio = bio;
        user.profileImage = profileImage;
        user.hobbies = hobbies;
        user.univCertificate = univCertificate;
        user.univ = univ;
        user.languages = languages;
        user.univVerification = verificationStatus;
        user.role = Role.USER;  // 정해진 값
        user.authProvider = authProvider;
        user.userStatus = UserStatus.ACTIVE;  // 정해진 값
        return user;
    }

    public int calculateAge() {
        return Period.between(this.DOB, LocalDate.now()).getYears();
    }
}
