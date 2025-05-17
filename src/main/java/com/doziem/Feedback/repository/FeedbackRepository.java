package com.doziem.Feedback.repository;

import com.doziem.Feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    List<Feedback> findAllByOrderByCreatedAtDesc();

    @Query("SELECT f FROM Feedback f WHERE " +
            "(f.rating = :rating OR CAST(f.createdAt AS date) = :createdDate) " +
            "ORDER BY f.createdAt DESC")
    List<Feedback> findByRatingOrCreatedAt(
            @Param("rating") Integer rating,
            @Param("createdDate") LocalDate createdDate);

    }
