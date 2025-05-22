package com.doziem.Feedback.service;

import com.doziem.Feedback.controller.AdminController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FeedbackSecurityTest {

    @Autowired
    private AdminController adminController;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllFeedback_AsAdmin_ShouldNotThrowException() {
        assertDoesNotThrow(() -> adminController.getAllFeedback());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllFeedback_AsUser_ShouldThrowAccessDenied() {
        assertThrows(AccessDeniedException.class, () -> adminController.getAllFeedback());
    }
}
