package com.javashell.jai.eventmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;

import com.javashell.jai.eventmanagement.LLMRequest.Result;
import com.javashell.jai.requests.ChatRequest;
import com.javashell.jai.requests.ChatToolsRequest;

public class LLMRequestManager {
	private static ArrayList<LLMRequest> queue = new ArrayList<LLMRequest>();
	private static ArrayList<LLMRequest> completed = new ArrayList<LLMRequest>();
	private static HashMap<LLMRequest, Result> results = new HashMap<LLMRequest, Result>();

	private static HashSet<LLMEventHandler> handlers = new HashSet<LLMEventHandler>();

	private static boolean doProcess = true, isWaiting = true;;
	private static int retryCount = 3;
	private static int sleepInterval = 3;
	private static int completedArrayMaxSize = 100;

	public static void addRequestToQueue(LLMRequest request) {
		fireEvent(LLMEvents.REQUEST_QUEUED);
		queue.add(request);
	}

	public static void startRequestProcessor() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (doProcess) {
					if (queue.size() != 0) {
						if (isWaiting) {
							fireEvent(LLMEvents.REQUEST_IN_PROCESS);
							isWaiting = false;
						}
						var toProcess = queue.removeLast();
						var result = toProcess.processRequest();
						int retries = 0;
						while (result.success == false) {
							if (retries >= retryCount) {
								break;
							}
							System.err.println("Request failed: \n" + result.result);
							retries++;
							result = toProcess.processRequest();
						}

						if (completed.size() >= completedArrayMaxSize) {
							results.remove(completed.removeFirst());
						}
						completed.add(toProcess);
						results.put(toProcess, result);

						if (toProcess.getBackingRequest() instanceof ChatRequest) {
							((ChatRequest) toProcess.getBackingRequest()).updateMessageHistory("assistant",
									result.result);
						}

						fireEvent(LLMEvents.REQUEST_COMPLETED);
						if (result.success && toProcess.getBackingRequest() instanceof ChatToolsRequest) {
							if (((ChatToolsRequest) toProcess.getBackingRequest()).didLastRequestCallATool())
								queue.add(toProcess);
						}
					} else {
						if (!isWaiting) {
							fireEvent(LLMEvents.WAITING);
							isWaiting = true;
						}
						try {
							Thread.sleep(1000 * sleepInterval);
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, "Failed to Thread sleep, exiting...");
							System.exit(-1);
						}
					}
				}
			}
		});
		t.setName("LLMRequestProcessor");
		t.start();
	}

	public static void subscribeLLMEventHandler(LLMEventHandler handler) {
		handlers.add(handler);
	}

	public static void unsubscribeLLMEventHandler(LLMEventHandler handler) {
		handlers.remove(handler);
	}

	public static ArrayList<LLMRequest> getQueuedRequests() {
		return queue;
	}

	public static ArrayList<LLMRequest> getCompletedRequests() {
		return completed;
	}

	public static int getCurrentRetryCount() {
		return retryCount;
	}

	public static void setRetryCount(int count) {
		retryCount = count;
	}

	public static int getcompletedArrayMaxSize() {
		return completedArrayMaxSize;
	}

	public static void setCompletedArrayMaxSize(int size) {
		completedArrayMaxSize = size;
	}

	public static Result getResult(LLMRequest request) {
		return results.get(request);
	}

	public static void removeCompletedRequest(LLMRequest request) {
		results.remove(request);
		completed.remove(request);
	}

	private static void fireEvent(LLMEvents event) {
		for (var handler : handlers)
			handler.llmEvent(event);
	}

}
