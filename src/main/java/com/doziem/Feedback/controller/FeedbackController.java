package com.doziem.Feedback.controller;

import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponse submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        return feedbackService.submitFeedback(feedbackRequest);
    }
}
