package com.doziem.Feedback.service;

import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    FeedbackResponse submitFeedback(FeedbackRequest feedbackRequest);

    List<FeedbackResponse> getAllFeedback();

    FeedbackResponse getFeedbackById(UUID id);

    List<FeedbackResponse> getFeedbackByRatingOrDate(Integer rating, LocalDate createdDate);

}
