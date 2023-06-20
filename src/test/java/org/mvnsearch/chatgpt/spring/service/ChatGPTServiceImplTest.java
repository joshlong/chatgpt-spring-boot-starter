package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class ChatGPTServiceImplTest extends ProjectBootBaseTest {

	private final ChatGPTService chatGPTService;

	@Autowired
	ChatGPTServiceImplTest(ChatGPTService chatGPTService) {
		this.chatGPTService = chatGPTService;
	}

	@Test
	void testSimpleChat() {
		final ChatCompletionRequest request = ChatCompletionRequest.of("What's Java Language?");
		final ChatCompletionResponse response = this.chatGPTService.chat(request).block();
		System.out.println(response.getReplyText());
	}

	@Test
	void testCompileJava() {
		final String prompt = "Give me a simple Java example, and compile the generated source code";
		final ChatCompletionRequest request = ChatCompletionRequest.functions(prompt, List.of("compile_java"));
		final ChatCompletionResponse response = this.chatGPTService.chat(request).block();
		System.out.println(response.getReplyCombinedText());
	}

	@Test
	public void testChatWithFunctions() throws Exception {
		final String prompt = "Hi Jackie, could you write an email to Libing(libing.chen@gmail.com) and Sam(linux_china@hotmail.com) and invite them to join Mike's birthday party at 4 tomorrow? Thanks!";
		final ChatCompletionRequest request = ChatCompletionRequest.functions(prompt, List.of("send_email"));
		final ChatCompletionResponse response = this.chatGPTService.chat(request).block();
		System.out.println(response.getReplyCombinedText());
	}

}
