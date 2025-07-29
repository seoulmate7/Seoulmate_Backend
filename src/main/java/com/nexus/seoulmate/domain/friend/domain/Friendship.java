package com.nexus.seoulmate.domain.friend.domain;

import com.nexus.seoulmate.domain.member.domain.Member;
import jakarta.persistence.*;

@Entity
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID_1")
    private Member userId1;

    @ManyToOne
    @JoinColumn(name = "USER_ID_2")
    private Member userId2;

    private int chemistry;
}
