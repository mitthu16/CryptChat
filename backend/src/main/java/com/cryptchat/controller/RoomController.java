package com.cryptchat.controller;

import com.cryptchat.model.ChatMessage;
import com.cryptchat.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class RoomController {

    private final ChatService chatService;

    public RoomController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/rooms/{roomName}/history")
    public List<ChatMessage> getRoomHistory(@PathVariable String roomName) {
        return chatService.getRoomHistory(roomName);
    }

    @GetMapping("/rooms")
    public List<String> getActiveRooms() {
        return chatService.getActiveRooms();
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "CryptChat Spring Boot Server is running!";
    }
}