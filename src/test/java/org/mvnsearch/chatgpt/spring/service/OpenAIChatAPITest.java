package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.springframework.beans.factory.annotation.Autowired;

class OpenAIChatAPITest extends ProjectBootBaseTest {

	private final OpenAIChatAPI openAIChatAPI;

	@Autowired
	OpenAIChatAPITest(OpenAIChatAPI openAIChatAPI) {
		this.openAIChatAPI = openAIChatAPI;
	}

	@Test
	void testStream() throws Exception {
		final ChatCompletionRequest request = ChatCompletionRequest
			.of("What's Java Language? Please give me simple example, and guide me how to run the example.");
		request.setStream(true);
		openAIChatAPI.stream(request)//
			.subscribe(response -> {//
				System.out.println(response.getReplyText());
			});
		Thread.sleep(60000);
	}

}
