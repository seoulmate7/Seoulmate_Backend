package com.nexus.seoulmate.friend.domain.entity;

import com.nexus.seoulmate.common.domain.BaseEntity;
import com.nexus.seoulmate.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Friendship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_1")
    private Member userId1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_2")
    private Member userId2;
}
