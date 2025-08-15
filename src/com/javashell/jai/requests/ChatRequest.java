package com.javashell.jai.requests;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.javashell.jai.chat.ChatMessage;

public class ChatRequest extends AIRequest {
	private static String chatPromptTemplate;
	static {
		try {
			chatPromptTemplate = new String(
					ChatRequest.class.getResourceAsStream("/chat_prompt_template").readAllBytes());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("FAILED TO LOAD TEMPLATES");
			System.exit(-1);
		}
	}

	protected String populatedRequest = "";
	private ArrayList<ChatMessage> chatHistory = new ArrayList<ChatMessage>();

	public ChatRequest(String model, URI endpoint) {
		super(model, endpoint);
		populatedRequest = chatPromptTemplate.replace("$MODEL", "\"" + model + "\"");
	}

	@Override
	public InputStream streamRequest(String prompt) throws Exception {

		chatHistory.add(new ChatMessage("user", prompt));

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder(endpoint)
				.POST(HttpRequest.BodyPublishers.ofString(
						populatedRequest.replace("$STREAMING", "true").replace("$MESSAGES", getMessageHistoryJSON())))
				.build();

		HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
		return response.body();
	}

	@Override
	public String postRequest(String prompt) throws Exception {
		if (prompt != null)
			chatHistory.add(new ChatMessage("user", prompt));

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder(endpoint)
				.POST(HttpRequest.BodyPublishers.ofString(
						populatedRequest.replace("$STREAMING", "false").replace("$MESSAGES", getMessageHistoryJSON())))
				.build();

		System.out
				.println(populatedRequest.replace("$STREAMING", "false").replace("$MESSAGES", getMessageHistoryJSON()));

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}

	private JSONParser parser = new JSONParser();

	@Override
	public String cleanResponse(String response) throws ParseException {
		JSONObject json = (JSONObject) parser.parse(response);
		JSONObject message = (JSONObject) json.get("message");
		return message.get("content").toString();
	}

	public void updateMessageHistory(String role, String message) {
		chatHistory.add(new ChatMessage(role, message));
	}

	public void updateMessageHistory(ChatMessage message) {
		chatHistory.add(message);
	}

	public ArrayList<ChatMessage> getChatHistory() {
		return chatHistory;
	}

	public void setMessageHistory(ArrayList<ChatMessage> history) {
		chatHistory = history;
	}

	public void clearMessageHistory() {
		chatHistory.clear();
	}

	private String getMessageHistoryJSON() {
		String history = "";
		for (ChatMessage chat : chatHistory) {
			history += chat.getJSONFormatted() + ",";
		}
		history = history.substring(0, history.length() - 1);

		// System.out.println(history);

		return history;
	}

}
