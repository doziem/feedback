package com.doziem.Feedback.exception;

public class FeedbackNotFoundException extends RuntimeException{
    public FeedbackNotFoundException(String message){
        super(message);
    }
}
