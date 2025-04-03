package com.project.appchat.controller;

import com.project.appchat.model.ChatMessage;
import com.project.appchat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
 * Gère l'envoi d'un message dans un salon
 */
@MessageMapping("/chat.sendMessage")
@PreAuthorize("hasRole('USER')")
public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatService.processMessage(chatMessage);
    }

    /**
     * Gère l'envoi d'un message privé
     */
    @MessageMapping("/chat.sendPrivateMessage")
    @PreAuthorize("hasRole('USER')")
    public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage) {
        return chatService.processPrivateMessage(chatMessage);
    }
}