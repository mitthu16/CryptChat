package com.cryptchat.model;

import jakarta.validation.constraints.NotBlank;

public class JoinRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public JoinRequest() {
		super();
	}

	@NotBlank(message = "Room is required")
    private String room;
}