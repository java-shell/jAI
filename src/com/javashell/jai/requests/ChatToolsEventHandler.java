package com.javashell.jai.requests;

import com.javashell.jai.chat.ChatTool.ResultantProperty;

public interface ChatToolsEventHandler {

	public void triggerEvent(ResultantProperty resultant, ChatToolsRequest requestor);

}
