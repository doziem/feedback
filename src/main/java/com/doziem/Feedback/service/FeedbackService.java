package com.doziem.Feedback.service;

import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;

import java.util.List;

public interface FeedbackService {
    FeedbackResponse submitFeedback(FeedbackRequest feedbackRequest);
    List<FeedbackResponse> getAllFeedback();
}
