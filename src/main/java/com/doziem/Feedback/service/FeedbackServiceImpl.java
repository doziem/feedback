package com.doziem.Feedback.service;

import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.model.Feedback;
import com.doziem.Feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService{

    private final FeedbackRepository repository;

    @Override
    public FeedbackResponse submitFeedback(FeedbackRequest feedbackRequest) {

        if (feedbackRequest.getRating() < 1 || feedbackRequest.getRating() > 5) {
            throw new InvalidFeedbackException("Rating must be between 1 and 5");
        }

        Feedback feedback = Feedback.builder()
                .userId(feedbackRequest.getUserId())
                .message(feedbackRequest.getMessage())
                .rating(feedbackRequest.getRating())
                .build();

        Feedback savedFeedback = repository.save(feedback);
        return mapToResponse(savedFeedback);
    }

    @Override
    public List<FeedbackResponse> getAllFeedback() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getFeedbackByRatingOrDate(Integer rating, LocalDate createdDate) {

        List<Feedback> feedbacks = repository.findByRatingOrCreatedAt(rating, createdDate);
        return feedbacks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {

        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setUserId(feedback.getUserId());
        response.setMessage(feedback.getMessage());
        response.setRating(feedback.getRating());
        response.setCreatedAt(feedback.getCreatedAt());

        return response;
    }
}
