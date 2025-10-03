package com.cryptchat.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private List<ChatMessage> messages = new ArrayList<>();
    private List<String> users = new ArrayList<>();
    
	public Room() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ChatMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ChatMessage> messages) {
		this.messages = messages;
	}
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}
}