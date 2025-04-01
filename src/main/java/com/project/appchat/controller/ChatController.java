package com.project.appchat.controller;

import com.project.appchat.model.ChatMessage;
import com.project.appchat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatService.processMessage(chatMessage);
    }

    @MessageMapping("/chat.addUser")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());
        return chatService.processMessage(chatMessage);
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage) {
        return chatService.processPrivateMessage(chatMessage);
    }
}
