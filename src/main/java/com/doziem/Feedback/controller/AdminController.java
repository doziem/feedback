package com.doziem.Feedback.controller;

import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.exception.MissingFilterException;
import com.doziem.Feedback.model.Feedback;
import com.doziem.Feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {

        try {
            List<FeedbackResponse> feedbacks =  feedbackService.getAllFeedback();
            return ResponseEntity.status(HttpStatus.OK).body(feedbacks);
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredFeedback(
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            return ResponseEntity.ok(feedbackService.getFeedbackByRatingOrDate(rating, date));
        } catch (InvalidFeedbackException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (MissingFilterException e){
            return ResponseEntity.badRequest().body(("At least one filter parameter (rating or date) must be provided"));
        }

        catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred");
        }
    }
}
