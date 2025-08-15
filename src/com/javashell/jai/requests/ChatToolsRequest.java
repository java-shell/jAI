package com.javashell.jai.requests;

import java.net.URI;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.javashell.jai.chat.ChatTool;
import com.javashell.jai.chat.ChatTool.ResultantProperty;

public class ChatToolsRequest extends ChatRequest {
	private boolean lastRequestCalledTool = false;
	private static String chatPromptTemplate;
	static {
		try {
			chatPromptTemplate = new String(
					ChatRequest.class.getResourceAsStream("/chat_tools_prompt_template").readAllBytes());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("FAILED TO LOAD TEMPLATES");
			System.exit(-1);
		}
	}

	private HashMap<String, ChatTool> tools = new HashMap<String, ChatTool>();

	public ChatToolsRequest(String model, URI endpoint, ChatTool... tools) {
		super(model, endpoint);
		populatedRequest = chatPromptTemplate.replace("$MODEL", "\"" + model + "\"");
		String toolString = new String();
		for (ChatTool t : tools) {
			this.tools.put(t.getFuncName(), t);
			toolString += t.getJSONDeclaration() + ",";
		}
		//System.out.println(toolString);
		toolString = toolString.substring(0, toolString.length() - 1).replaceAll("\\n", ", ").replaceAll("\\t", "")
				.replaceAll("\"", "\\\"");
		populatedRequest = populatedRequest.replace("$TOOLS", toolString);
	}

	JSONParser parser = new JSONParser();

	@Override
	public String cleanResponse(String response) throws ParseException {
		try {
			//System.out.println(response);
			JSONObject json = (JSONObject) parser.parse(response);
			JSONObject message = (JSONObject) json.get("message");
			JSONArray toolsArray = (JSONArray) message.get("tool_calls");
			if (toolsArray == null) {
				lastRequestCalledTool = false;
			} else {
				lastRequestCalledTool = true;

				for (var tool : toolsArray) {
					JSONObject toolTable = (JSONObject) ((JSONObject) tool).get("function");
					String funcName = toolTable.get("name").toString();
					JSONObject arguments = (JSONObject) toolTable.get("arguments");
					var argKeys = arguments.keySet();
					for (var key : argKeys) {
						var resultant = new ResultantProperty();
						resultant.propName = key.toString();
						resultant.result = arguments.get(key).toString();
						tools.get(funcName).triggerEvent(resultant, this);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.cleanResponse(response);
	}

	public boolean didLastRequestCallATool() {
		return lastRequestCalledTool;
	}

}
