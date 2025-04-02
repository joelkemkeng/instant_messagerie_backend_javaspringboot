package com.project.appchat.config;

import com.project.appchat.model.ChatMessage;
import com.project.appchat.model.ChatMessage.MessageType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("WebSocket connection established");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = null;
        
        if (headerAccessor != null && headerAccessor.getSessionAttributes() != null) {
            username = (String) headerAccessor.getSessionAttributes().get("username");
            
            if (username != null) {
                logger.info("User Disconnected : {}", username);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(MessageType.LEAVE);
                chatMessage.setSenderName(username);
                chatMessage.setContent(username + " a quitté le salon");

                // Envoyer un message global puisque nous ne savons pas dans quel salon l'utilisateur était
                messagingTemplate.convertAndSend("/topic/public", chatMessage);
            }
        }
    }
}
