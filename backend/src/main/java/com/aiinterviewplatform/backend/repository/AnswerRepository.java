package com.aiinterviewplatform.backend.repository;

import com.aiinterviewplatform.backend.entity.Answer;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    Optional<Answer> findByQuestion(InterviewQuestion question);
}