package com.doziem.Feedback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID id;
    private String userId;
    private String message;
    private int rating;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Add builder-style methods for testability
    public Feedback withCreatedAt(LocalDateTime createdAt) {
        return Feedback.builder()
                .id(this.id)
                .userId(this.userId)
                .message(this.message)
                .rating(this.rating)
                .createdAt(createdAt)
                .build();
    }
}
