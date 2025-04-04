package com.project.appchat.controller;

import com.project.appchat.dto.UserDto;
import com.project.appchat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final UserService userService;

    @GetMapping("/recent")
    public ResponseEntity<List<UserDto>> getRecentConversations(Authentication authentication) {
        return ResponseEntity.ok(userService.getRecentContacts(authentication.getName()));
    }
}