package com.nexus.seoulmate.member.repository;

import com.nexus.seoulmate.member.domain.Hobby;
import com.nexus.seoulmate.member.domain.enums.HobbyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    Optional<Hobby> findByHobbyNameAndHobbyCategory(String hobbyName, HobbyCategory category);
}
