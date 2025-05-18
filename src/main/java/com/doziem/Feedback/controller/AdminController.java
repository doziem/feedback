package com.doziem.Feedback.controller;

import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/feedback")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final FeedbackService feedbackService;

    @GetMapping
    public List<FeedbackResponse> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }

    @GetMapping("/filter")
    public List<?> getFilteredFeedback(
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            if (rating == null && date == null) {
                return ResponseEntity.ok(feedbackService.getAllFeedback()).getBody();
            }
            return ResponseEntity.ok(feedbackService.getFeedbackByRatingOrDate(rating, date)).getBody();
        } catch (InvalidFeedbackException e) {
            return Collections.singletonList(ResponseEntity.badRequest().body(e.getMessage()));
        } catch (Exception e) {
            return Collections.singletonList(ResponseEntity.internalServerError().body("An unexpected error occurred"));
        }
    }
}
