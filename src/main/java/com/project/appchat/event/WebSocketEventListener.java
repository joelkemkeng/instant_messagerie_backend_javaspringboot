package com.project.appchat.event;

import com.project.appchat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getSessionAttributes() != null ? 
            (String) headerAccessor.getSessionAttributes().get("username") : null;
        
        if (username != null) {
            log.info("User Disconnected : " + username);
            ChatMessage chatMessage = ChatMessage.builder()
                .type(ChatMessage.MessageType.LEAVE)
                .senderName(username)
                .build();
            
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
