package com.doziem.Feedback.service;

import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.FeedbackNotFoundException;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.model.Feedback;
import com.doziem.Feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService{

    private final FeedbackRepository repository;

    @Override
    public FeedbackResponse submitFeedback(FeedbackRequest feedbackRequest) {

        if (feedbackRequest == null) {
            throw new IllegalArgumentException("Feedback request cannot be null");
        }

        if (feedbackRequest.getRating() < 1 || feedbackRequest.getRating() > 5) {
            throw new InvalidFeedbackException("Rating must be between 1 and 5");
        }

        if (feedbackRequest.getUserId() == null) {
            throw new InvalidFeedbackException("User ID cannot be null");
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

        if (rating != null && (rating < 1 || rating > 5)) {
            throw new InvalidFeedbackException("Rating must be between 1 and 5");
        }

        List<Feedback> feedbacks = repository.findByRatingOrCreatedAt(rating, createdDate);
        return feedbacks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackResponse getFeedbackById(UUID id) throws FeedbackNotFoundException {
        Feedback feedback = repository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback not found with id: " + id));
        return mapToResponse(feedback);
    }

 public FeedbackResponse mapToResponse(Feedback feedback) {

        if (feedback == null) {
            throw new IllegalArgumentException("Feedback cannot be null");
        }

     try {
         return FeedbackResponse.builder()
                 .id(feedback.getId())
                 .userId(feedback.getUserId())
                 .message(feedback.getMessage())
                 .rating(feedback.getRating())
                 .createdAt(feedback.getCreatedAt())
                 .build();
     } catch (NullPointerException e) {
         throw new IllegalArgumentException("Invalid feedback data", e);
     }
    }
}
