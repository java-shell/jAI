package com.javashell.jai.requests;

import java.io.InputStream;

public interface AgentRequest {

	public InputStream streamRequest(String prompt) throws Exception;

	public String postRequest(String prompt) throws Exception;

	public String cleanResponse(String response) throws Exception;
}
