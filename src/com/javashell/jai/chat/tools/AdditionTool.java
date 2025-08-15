package com.javashell.jai.chat.tools;

import com.javashell.jai.chat.ChatTool;
import com.javashell.jai.chat.ChatToolMessage;
import com.javashell.jai.requests.ChatToolsEventHandler;
import com.javashell.jai.requests.ChatToolsRequest;

public class AdditionTool extends ChatTool {
	final static Property value1Property = new Property();
	final static Property value2Property = new Property();

	static {
		value1Property.propName = "Value1";
		value1Property.description = "The first value";
		value1Property.propType = "int";

		value2Property.propName = "Value2";
		value2Property.description = "The second value";
		value2Property.propType = "int";
	}

	public AdditionTool() {
		super("Add", "Add 2 values (value1 + value2)", value1Property, value2Property);
		registerHandler(new ChatToolsEventHandler() {
			int value1 = 0;

			public void triggerEvent(ResultantProperty resultant, ChatToolsRequest toolsRequest) {
				if (resultant.propName.equals("Value1"))
					value1 = Integer.parseInt(resultant.result);
				else if (resultant.propName.equals("Value2")) {
					int sum = value1 + Integer.parseInt(resultant.result);
					toolsRequest.updateMessageHistory(new ChatToolMessage("tool", "" + sum, "Add"));
				}
			}
		});
	}

}
