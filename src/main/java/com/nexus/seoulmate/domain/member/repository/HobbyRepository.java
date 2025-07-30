package com.nexus.seoulmate.domain.member.repository;

import com.nexus.seoulmate.domain.member.domain.Hobby;
import com.nexus.seoulmate.domain.member.domain.enums.HobbyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    Optional<Hobby> findByHobbyNameAndHobbyCategory(String hobbyName, HobbyCategory category);
}
