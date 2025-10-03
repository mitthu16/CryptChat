package com.cryptchat.websocket;

import com.cryptchat.model.ChatMessage;
import com.cryptchat.model.JoinRequest;
import com.cryptchat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.join")
    @SendTo("/topic/room.{room}")
    public ChatMessage joinRoom(JoinRequest joinRequest, SimpMessageHeaderAccessor headerAccessor) {
        // Add user to room
        chatService.addUserToRoom(joinRequest.getRoom(), joinRequest.getUsername());
        
        // Store username in session
        headerAccessor.getSessionAttributes().put("username", joinRequest.getUsername());
        headerAccessor.getSessionAttributes().put("room", joinRequest.getRoom());
        
        // Create join notification
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.SYSTEM);
        message.setContent(joinRequest.getUsername() + " joined the room");
        message.setTimestamp(LocalDateTime.now());
        message.setRoom(joinRequest.getRoom());
        
        return message;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/room.{room}")
    public ChatMessage sendMessage(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String room = (String) headerAccessor.getSessionAttributes().get("room");
        
        if (username != null && room != null) {
            message.setUsername(username);
            message.setRoom(room);
            
            // Process message with security scanning
            ChatMessage processedMessage = chatService.processMessage(message);
            
            // If message is blocked, send notification only to sender
            if (processedMessage.getSecurity().getBlocked()) {
                sendBlockedNotification(processedMessage, headerAccessor);
                return null; // Don't broadcast blocked messages
            }
            
            return processedMessage;
        }
        
        return null;
    }

    @MessageMapping("/chat.leave")
    @SendTo("/topic/room.{room}")
    public ChatMessage leaveRoom(SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String room = (String) headerAccessor.getSessionAttributes().get("room");
        
        if (username != null && room != null) {
            chatService.removeUserFromRoom(room, username);
            
            ChatMessage message = new ChatMessage();
            message.setType(ChatMessage.MessageType.SYSTEM);
            message.setContent(username + " left the room");
            message.setTimestamp(LocalDateTime.now());
            message.setRoom(room);
            
            return message;
        }
        
        return null;
    }

    private void sendBlockedNotification(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        ChatMessage blockedNotification = new ChatMessage();
        blockedNotification.setType(ChatMessage.MessageType.SYSTEM);
        blockedNotification.setContent("Your message was blocked by CryptChat security");
        blockedNotification.setTimestamp(LocalDateTime.now());
        blockedNotification.setRoom(message.getRoom());
        
        // Send only to the user who sent the blocked message
        // This would require a more complex setup with @SendToUser and user destinations
    }
}