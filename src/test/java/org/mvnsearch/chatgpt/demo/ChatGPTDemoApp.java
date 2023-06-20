package org.mvnsearch.chatgpt.demo;

import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChatGPTDemoApp {

	public static void main(String[] args) {
		SpringApplication.run(ChatGPTDemoApp.class, args);
	}

	@Bean
	ApplicationRunner runner(ChatGPTService gpt) {
		return a -> {
			final String content = "please tell me a little bit about the nature of AI";
			gpt.chat(ChatCompletionRequest.of(content))//
				.map(ChatCompletionResponse::getReplyText)//
				.subscribe(System.out::println);

		};

	}

}
