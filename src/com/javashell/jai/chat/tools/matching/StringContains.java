package com.javashell.jai.chat.tools.matching;

import com.javashell.jai.chat.ChatTool;
import com.javashell.jai.chat.ChatToolMessage;
import com.javashell.jai.requests.ChatToolsEventHandler;
import com.javashell.jai.requests.ChatToolsRequest;

public class StringContains extends ChatTool {
	final static Property inputStringProperty = new Property();
	final static Property expressionStringProperty = new Property();

	static {
		inputStringProperty.propName = "InputString";
		inputStringProperty.description = "String to check against";
		inputStringProperty.propType = "string";
		inputStringProperty.isRequired = true;

		expressionStringProperty.propName = "Expression";
		expressionStringProperty.description = "String to check for";
		expressionStringProperty.propType = "string";
		expressionStringProperty.isRequired = true;
	}

	public StringContains() {
		super("StringContains", "Check if an input string contains a specified string", inputStringProperty);
		registerHandler(new ChatToolsEventHandler() {
			String string1, string2;

			public void triggerEvent(ResultantProperty resultant, ChatToolsRequest toolsRequest) {
				if (resultant.propName.equals("InputString")) {
					string1 = resultant.result;
				}
				if (resultant.propName.equals("Expression")) {
					string1 = resultant.result;
				}
				if (string1 != null && string2 != null) {
					boolean contains = string1.contains(string2);
					toolsRequest.updateMessageHistory(new ChatToolMessage("tool", "" + contains, "StringContains"));
				}
			}
		});
	}
}
