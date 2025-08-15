package com.javashell.jai.chat;

public class ChatImageMessage extends ChatMessage {

	public ChatImageMessage(String role, String image) {
		super(role, image);
	}

	public String getJSONFormatted() {
		return "{ \"role\": " + "\"" + role + "\"," + "\"images\": [\"" + message + "\"] }";
	}

}
