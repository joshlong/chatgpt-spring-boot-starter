package org.mvnsearch.chatgpt.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;

class ChatCompletionRequestTest {

	@Test
	void testToJson() throws Exception {
		final String prompt = "What's the Java Language?";
		final ObjectMapper objectMapper = GPTFunctionUtils.objectMapper;
		final ChatCompletionRequest request = ChatCompletionRequest.of("What's the Java Language?");
		request.setFunctionCall("hello_java");
		final String json = objectMapper//
			.writerWithDefaultPrettyPrinter()//
			.writeValueAsString(request);
		final ChatCompletionRequest completionRequest = objectMapper//
			.readValue(json, new TypeReference<ChatCompletionRequest>() {
			});//
		final String content = completionRequest.getMessages().iterator().next().getContent();
		Assertions.assertTrue(content.contains(prompt));
	}

}
