package org.mvnsearch.chatgpt.demo.service;

import jakarta.annotation.Nonnull;
import org.mvnsearch.chatgpt.model.function.GPTFunction;
import org.mvnsearch.chatgpt.model.function.Parameter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class GPTFunctions {

	public record CompileJavaRequest(
			@Parameter(required = true, value = "java file name or source code") String source) {
	}

	@GPTFunction(name = "compile_java", value = "Compile Java code")
	void compileJava(CompileJavaRequest request) {
		System.out.println("Compiling Java Code");
	}

	record SendEmailRequest(@Nonnull @Parameter("Recipients of email") List<String> recipients, //
			@Nonnull @Parameter("Subject of email") String subject, //
			@Parameter("Content of email") String content//
	) {
	}

	@GPTFunction(name = "send_email", value = "Send email to receiver")
	String sendEmail(SendEmailRequest request) {
		System.out.println("Recipients: " + String.join(",", request.recipients));
		System.out.println("Subject:" + request.subject);
		System.out.println("Content:\n" + request.content);
		return "Email sent to " + String.join(",", request.recipients) + " successfully!";
	}

}
