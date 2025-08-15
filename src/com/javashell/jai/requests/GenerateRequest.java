package com.javashell.jai.requests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GenerateRequest extends AIRequest {
	private static String generatePromptTemplate;
	static {
		try {
			generatePromptTemplate = new String(
					GenerateRequest.class.getResourceAsStream("/generate_prompt_template").readAllBytes());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("FAILED TO LOAD TEMPLATES");
			System.exit(-1);
		}
	}

	private String populatedRequest = "";

	public GenerateRequest(String modelName, URI endpoint) {
		super(modelName, endpoint);
		populatedRequest = generatePromptTemplate.replace("$MODEL", "\"" + modelName + "\"");
	}

	public InputStream streamRequest(String prompt) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder(endpoint)
				.POST(HttpRequest.BodyPublishers.ofString(
						populatedRequest.replace("$STREAMING", "true").replace("$PROMPT", "\"" + prompt + "\"")))
				.build();

		HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
		return response.body();
	}

	public String postRequest(String prompt) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder(endpoint)
				.POST(HttpRequest.BodyPublishers.ofString(
						populatedRequest.replace("$PROMPT", "\"" + prompt + "\"").replace("$STREAMING", "false")))
				.build();

		// System.out.println(populatedRequest.replace("$PROMPT", "\"" + prompt +
		// "\"").replace("$STREAMING", "false"));

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}

	private JSONParser parser = new JSONParser();

	public String cleanResponse(String response) throws ParseException {
		JSONObject json = (JSONObject) parser.parse(response);
		return json.get("response").toString();
	}

}
