package com.doziem.Feedback.controller;

import com.doziem.Feedback.service.FeedbackService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
class ControllerTestConfig {
    @Bean
    @Primary
    FeedbackService feedbackService() {
        return mock(FeedbackService.class);
    }
}