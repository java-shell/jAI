package com.javashell.jai.chat.tools.matching;

import com.javashell.jai.chat.ChatTool;
import com.javashell.jai.chat.ChatToolMessage;
import com.javashell.jai.requests.ChatToolsEventHandler;
import com.javashell.jai.requests.ChatToolsRequest;

public class StringHighlighter extends ChatTool {
	final static Property inputStringProperty = new Property();
	final static Property expressionStringProperty = new Property();
	final static Property highlightPrefixProperty = new Property();

	static {
		inputStringProperty.propName = "InputString";
		inputStringProperty.description = "String to check";
		inputStringProperty.propType = "string";
		inputStringProperty.isRequired = true;

		expressionStringProperty.propName = "Expression";
		expressionStringProperty.description = "String to find and highlight";
		expressionStringProperty.propType = "string";
		expressionStringProperty.isRequired = true;

		highlightPrefixProperty.propName = "HighlightFix";
		highlightPrefixProperty.description = "String or character to prefix & suffix a highlighted sequence with";
		highlightPrefixProperty.propType = "string";
		highlightPrefixProperty.isRequired = true;
	}

	public StringHighlighter() {
		super("Highlight",
				"Check a given string for specific sequences, and prefix & suffix each sequence with a specified character",
				inputStringProperty, expressionStringProperty, highlightPrefixProperty);
		registerHandler(new ChatToolsEventHandler() {
			ResultantProperty inputResult, expressionResult, highlightResult;

			public void triggerEvent(ResultantProperty resultant, ChatToolsRequest toolsRequest) {
				switch (resultant.propName) {
				case "InputString":
					inputResult = resultant;
					break;
				case "Expression":
					expressionResult = resultant;
					break;
				case "HighlightFix":
					highlightResult = resultant;
					break;
				}
				if (inputResult != null && expressionResult != null && highlightResult != null) {
					String input = inputResult.result;
					String expression = expressionResult.result;
					String fixCharacter = highlightResult.result;

					input = input.replaceAll(expression, fixCharacter + expression + fixCharacter);
					toolsRequest.updateMessageHistory(new ChatToolMessage("tool", input, "Highlight"));

					inputResult = null;
					expressionResult = null;
					highlightResult = null;
				}
			}
		});
	}
}
