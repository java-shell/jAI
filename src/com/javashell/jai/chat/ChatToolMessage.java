package com.javashell.jai.chat;

public class ChatToolMessage extends ChatMessage {
	private final String toolName;

	public ChatToolMessage(String role, String message, String toolName) {
		super(role, message);
		this.toolName = toolName;
	}

	public String getJSONFormatted() {
		return "{ \"role\": " + "\"" + role + "\"," + "\"content\": \"" + message + "\"," + "\"tool_name\": \""
				+ toolName + "\" }";
	}

}
