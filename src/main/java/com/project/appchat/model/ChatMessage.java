package com.project.appchat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String id;
    private String content;
    private String senderId;
    private String senderName;
    private String roomId;
    private MessageType type;
    private LocalDateTime timestamp;
    private String recipientId; // For private messages

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        PRIVATE
    }
}
