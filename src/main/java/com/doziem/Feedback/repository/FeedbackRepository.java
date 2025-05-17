package com.doziem.Feedback.repository;

import com.doziem.Feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    List<Feedback> findAllByOrderByCreatedAtDesc();
}
