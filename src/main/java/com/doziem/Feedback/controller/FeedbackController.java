package com.doziem.Feedback.controller;

import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.FeedbackNotFoundException;
import com.doziem.Feedback.exception.GlobalExceptionHandler;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest,
                                            BindingResult bindingResult) throws GlobalExceptionHandler {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        if(feedbackRequest.getUserId() == null || feedbackRequest.getMessage() ==null || feedbackRequest.getRating() ==null ){
            throw new GlobalExceptionHandler();
        }

        try {
            FeedbackResponse response = feedbackService.submitFeedback(feedbackRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidFeedbackException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(feedbackService.getFeedbackById(id));
        } catch (FeedbackNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
