package com.javashell.jai.requests;

import java.net.URI;

public abstract class AIRequest implements AgentRequest {
	protected String model;
	protected URI endpoint;

	public AIRequest(String model, URI endpoint) {
		this.model = model;
		this.endpoint = endpoint;
	}

}
