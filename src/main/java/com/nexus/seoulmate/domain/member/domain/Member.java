package com.nexus.seoulmate.domain.member.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "COUNTRY_ID")
    private Country countryId;

    @Column(nullable = false, length = 120)
    private String bio;

    @Column(nullable = false)
    private String profileImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberLanguage> languages = new ArrayList<>();

    @ManyToMany
    private List<Hobby> hobbies = new ArrayList<>();

    @Column(nullable = false)
    private String univCertificate;

    @ManyToOne
    @JoinColumn(name = "UNIV_ID")
    private University univId;

    @Column(nullable = false)
    private VerificationState isVerified;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private SignupProcess signupProcess;

    public Member(String email, String password, String firstName, String lastName,
                  LocalDate DOB, Country countryId, String bio, String profileImage, List hobbies,
                  String univCertificate, University univId, List languages, SignupProcess signupProcess){
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
        this.countryId = countryId;
        this.bio = bio;
        this.profileImage = profileImage;
        this.hobbies = hobbies;
        this.univCertificate = univCertificate;
        this.univId = univId;
        this.languages = languages;
        isVerified = VerificationState.SUBMITTED;
        isDeleted = false;
        role = Role.USER;
        this.signupProcess = signupProcess;
    }
}
