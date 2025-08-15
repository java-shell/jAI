package com.javashell.jai.chat.tools.fileio;

import java.io.File;

import com.javashell.jai.chat.ChatTool;
import com.javashell.jai.chat.ChatToolMessage;
import com.javashell.jai.requests.ChatToolsEventHandler;
import com.javashell.jai.requests.ChatToolsRequest;

public class CheckFile extends ChatTool {
	final static Property filePathProperty = new Property();

	static {
		filePathProperty.propName = "FilePath";
		filePathProperty.description = "Path of the file to check";
		filePathProperty.propType = "string";
	}

	public CheckFile() {
		super("CheckFile", "Check if a file exists", filePathProperty);
		registerHandler(new ChatToolsEventHandler() {
			public void triggerEvent(ResultantProperty resultant, ChatToolsRequest toolsRequest) {
				String filePath = resultant.result;
				boolean exists = new File(filePath).exists();
				toolsRequest.updateMessageHistory(new ChatToolMessage("tool", "" + exists, "CheckFile"));
			}
		});
	}

}
