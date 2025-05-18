package com.doziem.Feedback.controller;

import com.doziem.Feedback.config.SecurityConfig;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.GlobalExceptionHandler;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.service.FeedbackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.lang.reflect.Array.get;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {AdminController.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Add your exception handler
                .build();
    }

    private final UUID testId = UUID.randomUUID();
    private final LocalDate testDate = LocalDate.of(2023, 6, 15);
    private final String testUserId = "user-123";

    private FeedbackResponse createTestResponse(UUID id, Integer rating, String userId) {
        return FeedbackResponse.builder()
                .id(id)
                .userId(userId)
                .rating(rating)
                .createdAt(testDate.atStartOfDay())
                .build();
    }

    @Test
    void getAllFeedback_ReturnsFeedbackList() throws Exception {
        // Arrange
        FeedbackResponse response1 = createTestResponse(testId, 5, testUserId);
        FeedbackResponse response2 = createTestResponse(UUID.randomUUID(), 3, "user-456");
        when(feedbackService.getAllFeedback()).thenReturn(List.of(response1, response2));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/feedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$", hasSize(2)))
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(testId.toString()))
                .andExpect((ResultMatcher) jsonPath("$[0].rating").value(5))
                .andExpect((ResultMatcher) jsonPath("$[1].rating").value(3));

        verify(feedbackService).getAllFeedback();
    }

    @Test
    void getFilteredFeedback_ByRating_ReturnsFilteredList() throws Exception {
        // Arrange
        FeedbackResponse response = createTestResponse(testId, 4, testUserId);
        when(feedbackService.getFeedbackByRatingOrDate(eq(4), isNull()))
                .thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/feedback/filter")
                        .param("rating", "4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$", hasSize(1)))
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(testId.toString()))
                .andExpect((ResultMatcher) jsonPath("$[0].rating").value(4));

        verify(feedbackService).getFeedbackByRatingOrDate(4, null);
    }

    @Test
    void getFilteredFeedback_ByDate_ReturnsFilteredList() throws Exception {
        // Arrange
        FeedbackResponse response = createTestResponse(testId, 5, testUserId);
        when(feedbackService.getFeedbackByRatingOrDate(isNull(), eq(testDate)))
                .thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/feedback/filter")
                        .param("date", testDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$", hasSize(1)))
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(testId.toString()));

        verify(feedbackService).getFeedbackByRatingOrDate(null, testDate);
    }

    @Test
    void getFilteredFeedback_NoParams_ReturnsAllFeedback() throws Exception {
        // Arrange
        FeedbackResponse response = createTestResponse(testId, 5, testUserId);
        when(feedbackService.getAllFeedback()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/feedback/filter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$", hasSize(1)))
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(testId.toString()));

        verify(feedbackService).getAllFeedback();
    }

}


