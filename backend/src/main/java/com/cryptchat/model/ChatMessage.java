package com.cryptchat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
//import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {
    private String id;
    private String username;
    private String content;
    public ChatMessage() {
		super();
	}

	private MessageType type;
    private LocalDateTime timestamp;
    private SecurityScanResult security;
    private String room;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public SecurityScanResult getSecurity() {
		return security;
	}

	public void setSecurity(SecurityScanResult security) {
		this.security = security;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public enum MessageType {
        TEXT, SYSTEM, FILE, IMAGE
    }
}