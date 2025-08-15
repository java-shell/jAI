package com.javashell.jai.chat.tools.fileio;

import java.io.FileInputStream;
import java.util.Scanner;

import com.javashell.jai.chat.ChatTool;
import com.javashell.jai.chat.ChatToolMessage;
import com.javashell.jai.requests.ChatToolsEventHandler;
import com.javashell.jai.requests.ChatToolsRequest;

public class ReadFile extends ChatTool {
	final static Property filePathProperty = new Property();

	static {
		filePathProperty.propName = "FilePath";
		filePathProperty.description = "Path of the file to check";
		filePathProperty.propType = "string";
	}

	public ReadFile() {
		super("ReadFile", "Read a file", filePathProperty);
		registerHandler(new ChatToolsEventHandler() {
			public void triggerEvent(ResultantProperty resultant, ChatToolsRequest toolsRequest) {
				String filePath = resultant.result;
				try {
					Scanner sc = new Scanner(new FileInputStream(filePath));
					String completed = "";
					while (sc.hasNextLine()) {
						completed += sc.nextLine();
					}
					toolsRequest.updateMessageHistory(new ChatToolMessage("tool", completed, "ReadFile"));
				} catch (Exception e) {
					toolsRequest.updateMessageHistory(
							new ChatToolMessage("tool", "Failed to read file: " + e.getLocalizedMessage(), "ReadFile"));
				}
			}
		});
	}
}
