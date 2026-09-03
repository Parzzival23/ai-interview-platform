package com.aiinterviewplatform.backend.repository;

import com.aiinterviewplatform.backend.entity.Interview;
import com.aiinterviewplatform.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {
    Optional<Interview> findByIdAndUser(UUID id, User user);

    Page<Interview> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}