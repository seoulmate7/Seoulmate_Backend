package com.nexus.seoulmate.meeting.domain.repository;

import com.nexus.seoulmate.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
