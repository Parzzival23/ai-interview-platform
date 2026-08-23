package com.aiinterviewplatform.backend.repository;

import com.aiinterviewplatform.backend.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {
}