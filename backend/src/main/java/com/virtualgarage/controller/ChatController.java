package com.virtualgarage.controller;

import com.virtualgarage.dto.ChatMessage;
import com.virtualgarage.entity.User;
import com.virtualgarage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * WebSocket controller for real-time chat functionality.
 */
@Controller
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    
    public ChatController(SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }
    
    /**
     * Handle messages sent to the general chat room.
     * Messages sent to /app/chat.send are broadcast to /topic/chat
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/chat")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        logger.debug("Received chat message from user {}: {}", 
                chatMessage.getSenderId(), chatMessage.getContent());
        
        // Enrich message with timestamp
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Enrich with user info if available
        if (chatMessage.getSenderId() != null) {
            userRepository.findById(chatMessage.getSenderId()).ifPresent(user -> {
                chatMessage.setSenderName(user.getDisplayName());
                chatMessage.setSenderAvatar(user.getAvatarUrl());
                
                // Update last seen
                user.setLastSeenAt(LocalDateTime.now());
                userRepository.save(user);
            });
        }
        
        return chatMessage;
    }
    
    /**
     * Handle user joining the general chat.
     */
    @MessageMapping("/chat.join")
    @SendTo("/topic/chat")
    public ChatMessage joinChat(@Payload ChatMessage chatMessage, 
                                SimpMessageHeaderAccessor headerAccessor) {
        logger.info("User {} joining chat", chatMessage.getSenderName());
        
        // Add username to WebSocket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());
        headerAccessor.getSessionAttributes().put("userId", chatMessage.getSenderId());
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSenderName() + " joined the chat");
        chatMessage.setTimestamp(LocalDateTime.now());
        
        return chatMessage;
    }
    
    /**
     * Handle messages in a specific thread/room.
     * Messages sent to /app/chat.room.{roomId} are broadcast to /topic/chat.room.{roomId}
     */
    @MessageMapping("/chat.room.{roomId}")
    public void sendToRoom(@DestinationVariable String roomId, 
                           @Payload ChatMessage chatMessage) {
        logger.debug("Message to room {}: {}", roomId, chatMessage.getContent());
        
        chatMessage.setRoomId(roomId);
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Enrich with user info
        if (chatMessage.getSenderId() != null) {
            userRepository.findById(chatMessage.getSenderId()).ifPresent(user -> {
                chatMessage.setSenderName(user.getDisplayName());
                chatMessage.setSenderAvatar(user.getAvatarUrl());
            });
        }
        
        // Broadcast to room subscribers
        messagingTemplate.convertAndSend("/topic/chat.room." + roomId, chatMessage);
    }
    
    /**
     * Handle user joining a specific room.
     */
    @MessageMapping("/chat.room.{roomId}.join")
    public void joinRoom(@DestinationVariable String roomId, 
                         @Payload ChatMessage chatMessage,
                         SimpMessageHeaderAccessor headerAccessor) {
        logger.info("User {} joining room {}", chatMessage.getSenderName(), roomId);
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSenderName() + " joined the room");
        chatMessage.setRoomId(roomId);
        chatMessage.setTimestamp(LocalDateTime.now());
        
        messagingTemplate.convertAndSend("/topic/chat.room." + roomId, chatMessage);
    }
    
    /**
     * Handle user leaving a specific room.
     */
    @MessageMapping("/chat.room.{roomId}.leave")
    public void leaveRoom(@DestinationVariable String roomId, 
                          @Payload ChatMessage chatMessage) {
        logger.info("User {} leaving room {}", chatMessage.getSenderName(), roomId);
        
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setContent(chatMessage.getSenderName() + " left the room");
        chatMessage.setRoomId(roomId);
        chatMessage.setTimestamp(LocalDateTime.now());
        
        messagingTemplate.convertAndSend("/topic/chat.room." + roomId, chatMessage);
    }
    
    /**
     * Handle typing indicator.
     */
    @MessageMapping("/chat.room.{roomId}.typing")
    public void sendTypingIndicator(@DestinationVariable String roomId,
                                    @Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.TYPING);
        chatMessage.setRoomId(roomId);
        
        messagingTemplate.convertAndSend("/topic/chat.room." + roomId + ".typing", chatMessage);
    }
    
    /**
     * Send a direct message to a specific user.
     */
    @MessageMapping("/chat.private.{userId}")
    public void sendPrivateMessage(@DestinationVariable Long userId,
                                   @Payload ChatMessage chatMessage) {
        logger.debug("Private message to user {}: {}", userId, chatMessage.getContent());
        
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Enrich with sender info
        if (chatMessage.getSenderId() != null) {
            userRepository.findById(chatMessage.getSenderId()).ifPresent(user -> {
                chatMessage.setSenderName(user.getDisplayName());
                chatMessage.setSenderAvatar(user.getAvatarUrl());
            });
        }
        
        // Send to specific user
        messagingTemplate.convertAndSendToUser(
                userId.toString(), 
                "/queue/private", 
                chatMessage
        );
    }
}
