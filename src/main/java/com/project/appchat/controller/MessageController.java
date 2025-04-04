package com.project.appchat.controller;

import com.project.appchat.dto.MessageDto;
import com.project.appchat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    
    // Endpoint REST pour récupérer les messages d'un salon
    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<MessageDto>> getSalonMessages(
            @PathVariable UUID salonId,
            Authentication authentication) {
        return ResponseEntity.ok(messageService.getSalonMessages(salonId, authentication.getName()));
    }
    
    // Endpoint REST pour récupérer les messages privés avec un utilisateur
    @GetMapping("/private/{userId}")
    public ResponseEntity<List<MessageDto>> getPrivateMessages(
            @PathVariable UUID userId,
            Authentication authentication) {
        return ResponseEntity.ok(messageService.getPrivateMessages(userId, authentication.getName()));
    }
    
    // Endpoint REST pour envoyer un message dans un salon
    @PostMapping("/salon")
    public ResponseEntity<MessageDto> sendMessageToSalon(
            @Valid @RequestBody MessageDto messageDto,
            Authentication authentication) {
        return ResponseEntity.ok(messageService.sendMessageToSalon(messageDto, authentication.getName()));
    }
    
    // Endpoint REST pour envoyer un message privé
    @PostMapping("/private")
    public ResponseEntity<MessageDto> sendPrivateMessage(
            @Valid @RequestBody MessageDto messageDto,
            Authentication authentication) {
        return ResponseEntity.ok(messageService.sendPrivateMessage(messageDto, authentication.getName()));
    }
    
    // Endpoint REST pour supprimer un message
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID messageId,
            Authentication authentication) {
        messageService.deleteMessage(messageId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    // Endpoint WebSocket pour envoyer un message dans un salon
    @MessageMapping("/salon")
    public void handleSalonMessage(@Payload MessageDto messageDto, Authentication authentication) {
        messageService.sendMessageToSalon(messageDto, authentication.getName());
    }
    
    // Endpoint WebSocket pour envoyer un message privé
    @MessageMapping("/private")
    public void handlePrivateMessage(@Payload MessageDto messageDto, Authentication authentication) {
        messageService.sendPrivateMessage(messageDto, authentication.getName());
    }
}