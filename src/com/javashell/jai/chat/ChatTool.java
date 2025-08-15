package com.javashell.jai.chat;

import java.util.HashSet;

import com.javashell.jai.requests.ChatToolsEventHandler;
import com.javashell.jai.requests.ChatToolsRequest;

public class ChatTool {
	private Property[] properties;

	private String toolDeclaration;

	private String funcName;

	private HashSet<ChatToolsEventHandler> handlers = new HashSet<ChatToolsEventHandler>();

	public ChatTool(String funcName, String description, Property... properties) {
		this.properties = properties;
		this.funcName = funcName;

		toolDeclaration = "{ \"type\": \"function\"," + "\"function\": {" + "\"name\": \"" + funcName + "\","
				+ "\"description\": \"" + description + "\"," + "\"parameters\": {" + "\"type\": \"object\","
				+ "\"properties\": {";

		int index = 0;
		String requiredString = ",\"required\": [";
		boolean hasRequired = false;
		for (var property : properties) {
			index++;
			toolDeclaration += "\"" + property.propName + "\": {" + "\"type\": \"" + property.propType + "\","
					+ "\"description\": \"" + property.description + "\"";
			if (property.isEnum) {
				toolDeclaration += "," + "\"enum\": [";
				for (int i = 0; i < property.enumList.length; i++) {
					toolDeclaration += "\"" + property.enumList[i] + "\"";
					if (i + 1 == property.enumList.length)
						toolDeclaration += " ]";
					else
						toolDeclaration += ", ";
				}
			}
			if (property.isRequired) {
				requiredString += "\"" + property.propName + "\",";
				hasRequired = true;
			}
			if (index == properties.length)
				toolDeclaration += "}";
			else
				toolDeclaration += "}, ";
		}
		if (hasRequired) {
			toolDeclaration += "}" + requiredString.substring(0, requiredString.length() - 1) + " ]";
		} else
			toolDeclaration += "}";
		toolDeclaration += "}}}";
	}

	public Property[] getProperties() {
		return properties;
	}

	public String getFuncName() {
		return funcName;
	}

	public String getJSONDeclaration() {
		return toolDeclaration;
	}

	public void registerHandler(ChatToolsEventHandler handler) {
		handlers.add(handler);
	}

	public void deregisterHandler(ChatToolsEventHandler handler) {
		handlers.remove(handler);
	}

	public void triggerEvent(ResultantProperty resultant, ChatToolsRequest requestor) {
		for (var handler : handlers)
			handler.triggerEvent(resultant, requestor);
	}

	public static class Property {
		public String propName, propType, description;
		public boolean isRequired, isEnum;
		public String[] enumList;
	}

	public static class ResultantProperty {
		public String propName;
		public String result;
	}
}
