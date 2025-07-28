package com.nexus.seoulmate.meeting.domain.repository;

import com.nexus.seoulmate.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // userId 를 같이 불러오도록
    @EntityGraph(attributePaths = {"userId"})
    Optional<Meeting> findWithUserById(Long id);
}
