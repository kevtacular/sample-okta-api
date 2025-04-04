package com.example.oktaapi.controller;

import com.example.oktaapi.model.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * A controller class that defines public endpoints for the application.
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping
    public Message getPublicMessage() {
        return new Message("This is a public endpoint that doesn't require authentication");
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "API is running normally");
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
}
