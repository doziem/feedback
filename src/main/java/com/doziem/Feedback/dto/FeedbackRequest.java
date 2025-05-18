package com.doziem.Feedback.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;


    public FeedbackRequest(int rating, String message, String userId) {
    }
}
