package com.aiinterviewplatform.backend.repository;

import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewQuestionRepository
        extends JpaRepository<InterviewQuestion, UUID> {

    List<InterviewQuestion> findByInterviewIdOrderByQuestionOrder(UUID interviewId);
}