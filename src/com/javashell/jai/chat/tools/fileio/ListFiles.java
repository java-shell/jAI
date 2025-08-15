package com.javashell.jai.chat.tools.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import com.javashell.jai.chat.ChatTool;
import com.javashell.jai.chat.ChatToolMessage;
import com.javashell.jai.requests.ChatToolsEventHandler;
import com.javashell.jai.requests.ChatToolsRequest;

public class ListFiles extends ChatTool {
	final static Property filePathProperty = new Property();

	static {
		filePathProperty.propName = "FilePath";
		filePathProperty.description = "Path of the file to check";
		filePathProperty.propType = "string";
	}

	public ListFiles() {
		super("ListFiles", "Get a comma seperated list of files from the given directory", filePathProperty);
		registerHandler(new ChatToolsEventHandler() {
			public void triggerEvent(ResultantProperty resultant, ChatToolsRequest toolsRequest) {
				String filePath = resultant.result;
				File[] files = new File(filePath).listFiles();
				if (files == null) {
					toolsRequest.updateMessageHistory(
							new ChatToolMessage("tool", "Failed to read file list from directory", "ListFiles"));
				} else {
					String fileListCSV = "";
					for (var file : files)
						fileListCSV += file.getName() + ", ";
					fileListCSV = fileListCSV.substring(0, fileListCSV.length() - 2);
					System.out.println(fileListCSV);
					toolsRequest.updateMessageHistory(new ChatToolMessage("tool", fileListCSV, "ListFiles"));
				}
			}
		});
	}
}
