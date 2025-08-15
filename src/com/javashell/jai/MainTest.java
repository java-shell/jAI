package com.javashell.jai;

import java.net.URI;
import java.util.Scanner;

import com.javashell.jai.chat.tools.fileio.CheckFile;
import com.javashell.jai.chat.tools.fileio.ListFiles;
import com.javashell.jai.chat.tools.fileio.ReadFile;
import com.javashell.jai.chat.tools.fileio.WriteFile;
import com.javashell.jai.chat.tools.matching.StringContains;
import com.javashell.jai.chat.tools.matching.StringHighlighter;
import com.javashell.jai.eventmanagement.LLMEventHandler;
import com.javashell.jai.eventmanagement.LLMEvents;
import com.javashell.jai.eventmanagement.LLMRequest;
import com.javashell.jai.eventmanagement.LLMRequestManager;
import com.javashell.jai.requests.ChatToolsRequest;

public class MainTest {
	private final static String model = "qwen3:latest";
	private final static URI endpoint = URI.create("http://192.168.0.224:11434/api/chat");

	public static void main(String[] args) throws Exception {

		ChatToolsRequest chat = new ChatToolsRequest(model, endpoint, new CheckFile(), new ReadFile(), new WriteFile(),
				new ListFiles(), new StringContains(), new StringHighlighter());

		final var request = new LLMRequest(chat, null);

		Scanner sc = new Scanner(System.in);

		System.out.println("---Welcome to jAI console utility. Please begin prompting when ready.---");
		System.out.print(">");
		chat.updateMessageHistory("user", sc.nextLine());

		LLMRequestManager.subscribeLLMEventHandler(new LLMEventHandler() {

			@Override
			public void llmEvent(LLMEvents event) {
				System.out.println(event.name());
				if (event == LLMEvents.WAITING) {
					try {
						System.out.println(request.getLastResult().result);
						System.out.print(">");
						String input = sc.nextLine();
						chat.updateMessageHistory("user", input);
						LLMRequestManager.addRequestToQueue(request);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		});
		LLMRequestManager.startRequestProcessor();
		LLMRequestManager.addRequestToQueue(request);
	}

}
