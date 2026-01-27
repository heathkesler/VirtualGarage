package com.virtualgarage.dto;

import java.time.LocalDateTime;

/**
 * DTO for real-time chat messages.
 */
public class ChatMessage {
    
    public enum MessageType {
        CHAT,           // Regular chat message
        JOIN,           // User joined the room
        LEAVE,          // User left the room
        TYPING,         // User is typing
        SYSTEM          // System notification
    }
    
    private MessageType type;
    private String content;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String roomId;          // Thread ID or "general" for main chat
    private LocalDateTime timestamp;
    private Long replyToMessageId;
    
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatMessage(MessageType type, String content, Long senderId, String senderName, String roomId) {
        this.type = type;
        this.content = content;
        this.senderId = senderId;
        this.senderName = senderName;
        this.roomId = roomId;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderAvatar() {
        return senderAvatar;
    }
    
    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Long getReplyToMessageId() {
        return replyToMessageId;
    }
    
    public void setReplyToMessageId(Long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }
}
