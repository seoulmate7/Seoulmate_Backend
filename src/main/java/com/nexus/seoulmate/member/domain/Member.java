package com.nexus.seoulmate.member.domain;

import com.nexus.seoulmate.domain.member.domain.enums.*;
import com.nexus.seoulmate.member.domain.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@NoArgsConstructor
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
    private Map<String, Integer> languages = new HashMap<>();
    // 언어 + 언어 레벨

    @ManyToMany
    private List<Hobby> hobbies = new ArrayList<>();

    @Column(nullable = false)
    private String univCertificate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private University univ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus isVerified;

    @Column(nullable = false)
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus;

    public static Member createGoogleUser(String email, String password, String firstName, String lastName,
                                        LocalDate DOB, Countries country, String bio, String profileImage, List<Hobby> hobbies,
                                        String univCertificate, University univ, Map<String, Integer> languages,
                                        AuthProvider authProvider){
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
        user.isVerified = VerificationStatus.SUBMITTED;
        user.isDeleted = false;
        user.role = Role.USER;
        user.authProvider = authProvider;
        user.userStatus = UserStatus.ACTIVE;
        return user;
    }
}
