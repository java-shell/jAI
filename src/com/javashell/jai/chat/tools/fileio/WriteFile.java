package com.javashell.jai.chat.tools.fileio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.javashell.jai.chat.ChatTool;
import com.javashell.jai.chat.ChatToolMessage;
import com.javashell.jai.requests.ChatToolsEventHandler;
import com.javashell.jai.requests.ChatToolsRequest;

public class WriteFile extends ChatTool {
	final static Property filePathProperty = new Property();
	final static Property overwriteFileProperty = new Property();
	final static Property newFileContentsProperty = new Property();

	static {
		filePathProperty.propName = "FilePath";
		filePathProperty.description = "Path of the file to check";
		filePathProperty.propType = "string";
		filePathProperty.isRequired = true;

		overwriteFileProperty.propName = "Overwrite";
		overwriteFileProperty.description = "Overwrite an existing file if there is one";
		overwriteFileProperty.propType = "boolean";
		filePathProperty.isRequired = true;

		newFileContentsProperty.propName = "Contents";
		newFileContentsProperty.description = "The content to write to the file, specify and empty string if nothing is to be written";
		newFileContentsProperty.propType = "string";
		newFileContentsProperty.isRequired = true;
	}

	public WriteFile() {
		super("WriteFile", "Write to a file", filePathProperty, overwriteFileProperty, newFileContentsProperty);
		registerHandler(new ChatToolsEventHandler() {
			ResultantProperty resultFilePath, resultOverwrite, resultContents;

			public void triggerEvent(ResultantProperty resultant, ChatToolsRequest toolsRequest) {
				switch (resultant.propName) {
				case "FilePath":
					resultFilePath = resultant;
					break;
				case "Overwrite":
					resultOverwrite = resultant;
					break;
				case "Contents":
					resultContents = resultant;
					break;
				}
				if (resultFilePath != null && resultOverwrite != null && resultContents != null) {
					String filePath = resultFilePath.result;
					boolean doOverwrite = Boolean.valueOf(resultOverwrite.result);
					String contents = resultContents.result;

					if (!doOverwrite && new File(filePath).exists()) {
						toolsRequest.updateMessageHistory(new ChatToolMessage("tool",
								"File exists and overwrite is set to false, cannot complete task.", "WriteFile"));
					} else {
						try {
							FileOutputStream fout = new FileOutputStream(new File(filePath));
							PrintStream out = new PrintStream(fout);
							out.print(contents);
							out.flush();
							out.close();
							toolsRequest.updateMessageHistory(
									new ChatToolMessage("tool", "File written successfully", "WriteFile"));
						} catch (Exception e) {
							toolsRequest.updateMessageHistory(new ChatToolMessage("tool",
									"Failed to write file: " + e.getLocalizedMessage(), "WriteFile"));
						}
					}
					resultFilePath = null;
					resultOverwrite = null;
					resultContents = null;
				}
			}
		});
	}
}
