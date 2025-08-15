<h1>Interact and manage Ollama AI Agent chats</h1>
See com.javashell.ai.MainTest.java for a working example.<br>
Supports ChatTools integrations, and image models.</br>
Tested on qwen3 and gemma3
<h2>Usage</h2>

```java
  public static void main(String[] args){
    ChatToolsRequest chat = new ChatToolsRequest(model, endpoint, new CheckFile(), new ReadFile(), new WriteFile(),
				  new ListFiles(), new StringContains(), new StringHighlighter());
  
  final var request = new LLMRequest(chat, null);
  chat.updateMessageHistory("user", "Hi AI, write me a Hello World program in Java");
  LLMRequestManager.subscribeLLMEventHandler(new LLMEventHandler() {

  @Override
			public void llmEvent(LLMEvents event) {
				System.out.println(event.name());
				if (event == LLMEvents.WAITING) {
						System.out.println(request.getLastResult().result);
				}
			}

  });
  LLMRequestManager.startRequestProcessor();
	LLMRequestManager.addRequestToQueue(request);
```

This creates a ChatToolRequest with the specified ChatTools, and updates the Chat history to include a first prompt.</br>
Then, an LLMEventHandler is registered to listen for the chat completion response.
