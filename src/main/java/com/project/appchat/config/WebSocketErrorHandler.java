package com.project.appchat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

public class WebSocketErrorHandler extends StompSubProtocolErrorHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketErrorHandler.class);

    @Override
    public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, @NonNull Throwable ex) {
        logger.error("Erreur lors du traitement d'un message client WebSocket", ex);
        
        if (clientMessage != null) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
            StompCommand command = accessor != null ? accessor.getCommand() : null;
            
            logger.error("Commande STOMP: {}", command);
            logger.error("Message: {}", clientMessage);
        } else {
            logger.error("Pas de message client disponible");
        }
        
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }
}
