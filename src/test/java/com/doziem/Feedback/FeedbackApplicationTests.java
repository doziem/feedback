package com.doziem.Feedback;

import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.model.Feedback;
import com.doziem.Feedback.repository.FeedbackRepository;
import com.doziem.Feedback.service.FeedbackServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

@SpringBootTest
class FeedbackApplicationTests {


	@Mock
	private FeedbackRepository feedbackRepository;

	@InjectMocks
	private FeedbackServiceImpl feedbackService;

	@Test
	void submitFeedback_ValidInput_ReturnsFeedbackResponse() {
		UUID testId = UUID.randomUUID();
		FeedbackRequest request = new FeedbackRequest("user123", "Great service", 5);
		Feedback savedFeedback = Feedback.builder()
				.id(testId)
				.userId("user123")
				.message("Great service")
				.rating(5)
				.createdAt(LocalDateTime.now())
				.build();

		when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);

		FeedbackResponse response = feedbackService.submitFeedback(request);

		assertNotNull(response);
		assertEquals(testId, response.getId());
		assertEquals("user123", response.getUserId());
		assertEquals(5, response.getRating());
	}

	@Test
	void getFilteredFeedbacks_ByRating_ReturnsFilteredList() {
		UUID testId = UUID.randomUUID();
		Feedback feedback = Feedback.builder()
				.id(testId)
				.userId("user123")
				.message("Test")
				.rating(4)
				.createdAt(LocalDateTime.now())
				.build();

		when(feedbackRepository.findByRatingOrCreatedAt(4, null))
				.thenReturn(List.of(feedback));

		List<FeedbackResponse> responses = feedbackService.getFeedbackByRatingOrDate(4, null);

		assertEquals(1, responses.size());
		assertEquals(4, responses.get(0).getRating());
	}


	}
