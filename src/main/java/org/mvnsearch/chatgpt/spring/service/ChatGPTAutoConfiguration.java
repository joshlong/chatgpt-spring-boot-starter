package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionsStub;
import org.mvnsearch.chatgpt.model.function.Parameter;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@RegisterReflectionForBinding({ //
		Parameter.class, //
		GPTFunction.class, //
		ChatCompletionRequest.class, //
		ChatCompletionResponse.class, //
		ChatCompletionChoice.class, //
		ChatCompletionUsage.class, //
		ChatMessage.class, //
		FunctionCall.class, //
		ChatFunction.class, //
		ChatFunction.Parameters.class, //
		ChatFunction.JsonSchemaProperty.class, //
		ChatFunction.JsonArrayItems.class, //
		GPTFunctionsStub.class, //
		ChatGPTJavaFunction.class//
})
@AutoConfiguration
class ChatGPTAutoConfiguration {

	@Bean
	OpenAIChatAPI openAIChatAPI(@Value("${openai.api.key}") String openaiApiKey, WebClient.Builder builder) {
		WebClient webClient = builder.defaultHeader("Authorization", "Bearer " + openaiApiKey).build();
		return HttpServiceProxyFactory.builder()
			.clientAdapter(WebClientAdapter.forClient(webClient))
			.build()
			.createClient(OpenAIChatAPI.class);
	}

	@Bean
	ChatGPTService chatGPTService(OpenAIChatAPI openAIChatAPI, GPTFunctionsRegistry registry) throws Exception {
		return new ChatGPTServiceImpl(openAIChatAPI, registry);
	}

	@Bean
	static GPTFunctionsRegistry gptFunctionsRegistry() {
		return new GPTFunctionsRegistry();
	}

}
