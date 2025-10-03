package com.cryptchat.service;

import com.cryptchat.model.ChatMessage;
import com.cryptchat.model.Room;
import com.cryptchat.model.SecurityScanResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ChatService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final AtomicLong messageIdCounter = new AtomicLong(1);
    private final SecurityScannerService securityScanner;

    public ChatService(SecurityScannerService securityScanner) {
        this.securityScanner = securityScanner;
        // Initialize default room
        initializeDefaultRooms();
    }

    private void initializeDefaultRooms() {
        String[] defaultRooms = {"general", "security", "random"};
        for (String roomName : defaultRooms) {
            Room room = new Room();
            room.setName(roomName);
            room.setMessages(new ArrayList<>());
            room.setUsers(new ArrayList<>());
            rooms.put(roomName, room);
        }
    }

    public Room getRoom(String roomName) {
        return rooms.computeIfAbsent(roomName, name -> {
            Room room = new Room();
            room.setName(name);
            room.setMessages(new ArrayList<>());
            room.setUsers(new ArrayList<>());
            return room;
        });
    }

    public ChatMessage processMessage(ChatMessage message) {
        // Generate message ID
        message.setId(String.valueOf(messageIdCounter.getAndIncrement()));
        message.setTimestamp(LocalDateTime.now());
        
        // Perform security scan
        SecurityScanResult scanResult = securityScanner.scanMessage(message.getContent());
        message.setSecurity(scanResult);
        
        // Add to room history if not blocked
        if (!scanResult.getBlocked()) {
            Room room = getRoom(message.getRoom());
            room.getMessages().add(message);
            
            // Keep only last 100 messages per room
            if (room.getMessages().size() > 100) {
                room.setMessages(new ArrayList<>(
                    room.getMessages().subList(room.getMessages().size() - 100, room.getMessages().size())
                ));
            }
        }
        
        return message;
    }

    public void addUserToRoom(String roomName, String username) {
        Room room = getRoom(roomName);
        if (!room.getUsers().contains(username)) {
            room.getUsers().add(username);
        }
    }

    public void removeUserFromRoom(String roomName, String username) {
        Room room = rooms.get(roomName);
        if (room != null) {
            room.getUsers().remove(username);
        }
    }

    public List<ChatMessage> getRoomHistory(String roomName) {
        Room room = getRoom(roomName);
        return new ArrayList<>(room.getMessages());
    }

    public List<String> getActiveRooms() {
        return new ArrayList<>(rooms.keySet());
    }
}