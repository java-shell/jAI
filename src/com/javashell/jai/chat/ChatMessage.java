package com.javashell.jai.chat;

import org.json.simple.JSONObject;

public class ChatMessage {
	protected final String role, message;

	public ChatMessage(String role, String message) {
		this.role = role;
		this.message = JSONObject.escape(message);
	}

	public String getJSONFormatted() {
		return "{ \"role\": " + "\"" + role + "\"," + "\"content\": \"" + message + "\" }";
	}

	public String getRole() {
		return role;
	}

	public String getMessage() {
		return message;
	}

}
