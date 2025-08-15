package com.javashell.jai.eventmanagement;

import java.time.LocalDateTime;

import com.javashell.jai.requests.AIRequest;

public class LLMRequest {
	private AIRequest req;
	private String prompt;

	private LocalDateTime creationTime;

	public LLMRequest(AIRequest req, String prompt) {
		this.req = req;
		this.prompt = prompt;
		this.creationTime = LocalDateTime.now();
	}

	public Result processRequest() {
		Result result = new Result();
		try {
			String dirtyResult = req.postRequest(prompt);
			System.out.println(dirtyResult);
			result.result = req.cleanResponse(dirtyResult);
			result.dirtyResult = dirtyResult;
		} catch (Exception e) {
			result.success = false;
			result.result = e.getMessage();
			e.printStackTrace();
			return result;
		}
		return result;
	}

	public Result getLastResult() {
		return LLMRequestManager.getResult(this);
	}

	public String getPrompt() {
		return prompt;
	}

	public AIRequest getBackingRequest() {
		return req;
	}

	public class Result {
		public boolean success = true;
		public String result = "";
		public String dirtyResult = "";
	}

	public String toString() {
		return creationTime.toString();
	}

}
