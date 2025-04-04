package com.example.oktaapi.model;

import java.time.LocalDateTime;

/**
 * The {@code Message} class represents a message with content and a timestamp.
 * 
 * <p>
 * The timestamp is automatically set to the current date and time when a
 * {@code Message} object is created.
 * </p>
 */
public class Message {
    private String content;
    private LocalDateTime timestamp;

    // Default constructor
    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with content
    public Message(String content) {
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
