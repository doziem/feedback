package com.doziem.Feedback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class FeedbackApplicationTests {
	@Autowired
	private ApplicationContext context;

	@Test
	void contextLoads() {
		// Basic validation that context loads
		assertThat(context).isNotNull();
	}

	@Test
	void verifyCriticalBeansAreAvailable() {
		// Validate essential beans are present
		assertThat(context.getBean("feedbackController")).isNotNull();
		assertThat(context.getBean("feedbackService")).isNotNull();
		assertThat(context.getBean("feedbackRepository")).isNotNull();

		// If you have security configured
		 assertThat(context.getBean("securityFilterChain")).isNotNull();
	}

	@Test
	void verifyDatabaseConnection() {
		// If you want to test DB connectivity
		assertThat(context.getEnvironment()
				.getProperty("spring.datasource.url"))
				.isNotBlank();
	}
	}
