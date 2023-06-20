package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

class ChatGPTServiceImpl implements ChatGPTService {

	private final OpenAIChatAPI openAIChatAPI;

	private final GPTFunctionsRegistry registry;

	ChatGPTServiceImpl(OpenAIChatAPI openAIChatAPI, GPTFunctionsRegistry registry) throws Exception {
		this.openAIChatAPI = openAIChatAPI;
		this.registry = registry;
	}

	@Override
	public Mono<ChatCompletionResponse> chat(ChatCompletionRequest request) {
		injectFunctions(request);
		boolean functionsIncluded = request.getFunctions() != null;
		if (!functionsIncluded) {
			return this.openAIChatAPI.chat(request);
		} //
		else {
			return this.openAIChatAPI//
				.chat(request)//
				.doOnNext(response -> {
					for (final ChatMessage chatMessage : response.getReply()) {
						injectFunctionCallLambda(chatMessage);
					}
				});
		}
	}

	@Override
	public Flux<ChatCompletionResponse> stream(ChatCompletionRequest request) {
		request.setStream(true);
		injectFunctions(request);
		final boolean functionsIncluded = request.getFunctions() != null;
		if (!functionsIncluded) {
			return this.openAIChatAPI//
				.stream(request)//
				.onErrorContinue((e, obj) -> {
				});//
		} //
		else {
			return this.openAIChatAPI//
				.stream(request)//
				.onErrorContinue((e, obj) -> {
				})//
				.doOnNext(response -> {
					for (ChatMessage chatMessage : response.getReply()) {
						injectFunctionCallLambda(chatMessage);
					}
				});
		}
	}

	private void injectFunctions(ChatCompletionRequest request) {
		final List<String> functionNames = request.getFunctionNames();
		if (functionNames != null && !functionNames.isEmpty()) {
			for (final String functionName : functionNames) {
				ChatFunction chatFunction = this.registry.getChatFunction(functionName);
				if (chatFunction != null) {
					request.addFunction(chatFunction);
				}
			}
		}
	}

	private void injectFunctionCallLambda(ChatMessage chatMessage) {
		final FunctionCall functionCall = chatMessage.getFunctionCall();
		if (functionCall != null) {
			final String functionName = functionCall.getName();
			final ChatGPTJavaFunction jsonSchemaFunction = this.registry.getChatGPTJavaFunction(functionName);
			if (jsonSchemaFunction != null) {
				functionCall.setFunctionStub(() -> jsonSchemaFunction.call(functionCall.getArguments()));
			}
		}
	}

}
