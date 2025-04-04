package com.project.appchat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class CorsTestController {

    @GetMapping("/test-cors")
    public Map<String, String> testCors() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS est correctement configuré!");
        return response;
    }
}