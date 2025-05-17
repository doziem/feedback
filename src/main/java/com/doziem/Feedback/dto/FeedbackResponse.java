package com.doziem.Feedback.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {

    private UUID id;
    private String userId;
    private String message;
    private int rating;
    private LocalDateTime createdAt;
}
