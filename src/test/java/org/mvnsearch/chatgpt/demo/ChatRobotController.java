package org.mvnsearch.chatgpt.demo;

import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
class ChatRobotController {

	private final ChatGPTService gpt;

	@Autowired
	ChatRobotController(ChatGPTService getChatGPTService) {
		this.gpt = getChatGPTService;
	}

	@PostMapping("/chat")
	Mono<String> chat(@RequestBody String content) {
		return this.gpt.chat(ChatCompletionRequest.of(content)).map(ChatCompletionResponse::getReplyText);
	}

	// stream chat by server-sent events
	@GetMapping("/stream-chat")
	Flux<String> streamChat(@RequestParam String content) {
		return this.gpt.stream(ChatCompletionRequest.of(content)).map(ChatCompletionResponse::getReplyText);
	}

}
